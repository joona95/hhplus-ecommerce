package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemRepository;
import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;
    private final PopularItemJpaRepository popularItemJpaRepository;
    private final PopularItemQuerydslRepository popularItemQuerydslRepository;

    public ItemRepositoryImpl(ItemJpaRepository itemJpaRepository, PopularItemJpaRepository popularItemJpaRepository, PopularItemQuerydslRepository popularItemQuerydslRepository) {
        this.itemJpaRepository = itemJpaRepository;
        this.popularItemJpaRepository = popularItemJpaRepository;
        this.popularItemQuerydslRepository = popularItemQuerydslRepository;
    }

    @Override
    public Optional<Item> findById(long id) {
        return itemJpaRepository.findById(id);
    }

    @Override
    public List<PopularItem> findPopularItems() {
        return popularItemQuerydslRepository.findPopularItems();
    }

    @Override
    public Item findByIdWithLock(long id) {
        return itemJpaRepository.findByIdWithLock(id);
    }

    @Override
    public List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics) {
        return popularItemJpaRepository.saveAll(popularItemStatistics);
    }

    @Override
    public Item saveItem(Item item) {
        return itemJpaRepository.save(item);
    }
}
