package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.order.OrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.domain.item.ItemCommand.*;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    public List<PopularItem> findPopularItems() {
        return Optional.ofNullable(itemRepository.findPopularItems())
                .orElse(List.of());
    }

    @Transactional
    public List<Item> decreaseStocks(List<StockDecreaseCommand> commands) {

        List<Long> itemIds = commands.stream()
                .map(StockDecreaseCommand::itemId)
                .toList();

        List<Item> items = itemRepository.findByIdInWithLock(itemIds);

        Map<Long, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));

        for (StockDecreaseCommand command : commands) {

            Item item = itemMap.get(command.itemId());
            item.decreaseStock(command.count());
        }

        return items;
    }

    @Transactional
    public List<PopularItem> createPopularItemStatistics(List<OrderItem> orderItems) {

        Map<Long, OrderItem> orderItemMapByItemId = orderItems.stream().collect(Collectors.toMap(OrderItem::getItemId, Function.identity(), (o1, o2) -> o1));

        Map<Long, Integer> orderCountByItemId = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getItemId, Collectors.summingInt(OrderItem::getCount)));

        List<PopularItem> popularItems = orderItemMapByItemId.keySet().stream()
                .map(itemId -> PopularItem.of(orderItemMapByItemId.get(itemId), orderCountByItemId.get(itemId)))
                .toList();

        return itemRepository.savePopularItems(popularItems);
    }
}
