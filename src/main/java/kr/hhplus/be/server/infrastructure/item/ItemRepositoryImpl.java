package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;

    public ItemRepositoryImpl(ItemJpaRepository itemJpaRepository) {
        this.itemJpaRepository = itemJpaRepository;
    }

    @Override
    public Optional<Item> findById(long id) {
        return itemJpaRepository.findById(id);
    }

    @Override
    public Item findByIdWithLock(long id) {
        return itemJpaRepository.findByIdWithLock(id);
    }

    @Override
    public Item saveItem(Item item) {
        return itemJpaRepository.save(item);
    }
}
