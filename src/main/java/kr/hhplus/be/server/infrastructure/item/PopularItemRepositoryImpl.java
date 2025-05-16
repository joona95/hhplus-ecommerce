package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemRepository;
import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import kr.hhplus.be.server.infrastructure.store.RedisStoreRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PopularItemRepositoryImpl implements PopularItemRepository {

    private static final String POPULAR_ITEMS_KEY_PREFIX = "popular-items:";

    private final PopularItemJpaRepository popularItemJpaRepository;
    private final PopularItemQuerydslRepository popularItemQuerydslRepository;
    private final RedisStoreRepository redisStoreRepository;

    public PopularItemRepositoryImpl(PopularItemJpaRepository popularItemJpaRepository, PopularItemQuerydslRepository popularItemQuerydslRepository, RedisStoreRepository redisStoreRepository) {
        this.popularItemJpaRepository = popularItemJpaRepository;
        this.popularItemQuerydslRepository = popularItemQuerydslRepository;
        this.redisStoreRepository = redisStoreRepository;
    }

    @Override
    public List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics) {
        return popularItemJpaRepository.saveAll(popularItemStatistics);
    }

    @Override
    public void savePopularItemScore(PopularItem popularItem) {
        redisStoreRepository.addScoreInSoredSet(
                POPULAR_ITEMS_KEY_PREFIX + LocalDate.now(),
                popularItem.getItemId(),
                popularItem.getOrderCount());
    }

    @Override
    public List<PopularItem> findPopularItems() {
        return popularItemQuerydslRepository.findPopularItems();
    }

    @Override
    public List<PopularItem> findPopularItemScore(LocalDate date) {
        return redisStoreRepository.getSoredSet(POPULAR_ITEMS_KEY_PREFIX + date).stream()
                .map(e -> new PopularItem(e.getValue(), e.getScore().intValue()))
                .toList();
    }
}
