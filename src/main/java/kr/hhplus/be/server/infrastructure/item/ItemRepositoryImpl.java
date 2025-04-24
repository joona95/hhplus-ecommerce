package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemRepository;
import kr.hhplus.be.server.domain.item.PopularItem;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final ItemJpaRepository itemJpaRepository;
    private final PopularItemJpaRepository popularItemJpaRepository;

    public ItemRepositoryImpl(ItemJpaRepository itemJpaRepository, PopularItemJpaRepository popularItemJpaRepository) {
        this.itemJpaRepository = itemJpaRepository;
        this.popularItemJpaRepository = popularItemJpaRepository;
    }

    @Override
    public Optional<Item> findById(long id) {
        return itemJpaRepository.findById(id);
    }

    @Override
    public List<PopularItem> findPopularItems() {
        return popularItemJpaRepository.findByOrderDateBetween(LocalDate.now().minusDays(3), LocalDate.now().minusDays(1));
    }

    @Override
    public List<Item> findByIdInWithLock(List<Long> ids) {
        return itemJpaRepository.findAllByIdWithLock(ids);
    }

    @Override
    public List<PopularItem> savePopularItems(List<PopularItem> popularItems) {
        return popularItemJpaRepository.saveAll(popularItems);
    }
}
