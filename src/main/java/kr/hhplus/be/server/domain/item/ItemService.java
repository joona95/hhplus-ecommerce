package kr.hhplus.be.server.domain.item;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.hhplus.be.server.domain.item.ItemCommand.*;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ApplicationEventPublisher publisher;

    public ItemService(ItemRepository itemRepository, ApplicationEventPublisher publisher) {
        this.itemRepository = itemRepository;
        this.publisher = publisher;
    }

    @Cacheable(value = "cache:item", key = "#id")
    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
    }

    @Transactional
    public Item decreaseStock(StockDecreaseCommand command) {

        Item item = itemRepository.findByIdWithLock(command.itemId());
        item.decreaseStock(command.count());

        publisher.publishEvent(new ItemEvent.StockDecreasedEvent(command.itemId(), command.count()));

        return item;
    }

    @CacheEvict(value = "cache:item", key = "#itemId")
    @Transactional
    public Item updateItem(Long itemId, ItemUpdateCommand command) {

        Item item = findById(itemId);

        item.update(command);

        return itemRepository.saveItem(item);
    }
}
