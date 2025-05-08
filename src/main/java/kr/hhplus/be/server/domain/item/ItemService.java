package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.order.OrderItemStatistics;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.domain.item.ItemCommand.*;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Cacheable(value = "cache:item", key = "#id")
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public List<PopularItemDetail> findPopularItems() {

        List<PopularItem> popularItems = Optional.ofNullable(itemRepository.findPopularItems()).orElse(List.of());

        return popularItems.stream()
                .map(popularItem -> PopularItemDetail.of(popularItem, findById(popularItem.itemId())))
                .toList();
    }

    @Transactional
    public Item decreaseStock(StockDecreaseCommand command) {

        Item item = itemRepository.findByIdWithLock(command.itemId());
        item.decreaseStock(command.count());

        return item;
    }

    @Transactional
    public List<PopularItemStatistics> createPopularItems(OrderItemStatistics orderItemStatistics) {

        List<PopularItemStatistics> popularItemStatistics = orderItemStatistics.getItemIds().stream()
                .map(itemId -> PopularItemStatistics.of(itemId, orderItemStatistics.getOrderDate(itemId), orderItemStatistics.getTotalOrderCount(itemId)))
                .toList();

        return itemRepository.savePopularItems(popularItemStatistics);
    }

    @CacheEvict(value = "cache:item", key = "#itemId")
    @Transactional
    public Item updateItem(Long itemId, ItemUpdateCommand command) {

        Item item = findById(itemId);

        item.update(command);

        return itemRepository.saveItem(item);
    }
}
