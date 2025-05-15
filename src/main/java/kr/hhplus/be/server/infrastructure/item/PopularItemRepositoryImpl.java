package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemRepository;
import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Repository
public class PopularItemRepositoryImpl implements PopularItemRepository {

    private static final String POPULAR_ITEMS_KEY_PREFIX = "popular-items:";

    private final PopularItemJpaRepository popularItemJpaRepository;
    private final PopularItemQuerydslRepository popularItemQuerydslRepository;
    private final RedissonClient redissonClient;

    public PopularItemRepositoryImpl(PopularItemJpaRepository popularItemJpaRepository, PopularItemQuerydslRepository popularItemQuerydslRepository, RedissonClient redissonClient) {
        this.popularItemJpaRepository = popularItemJpaRepository;
        this.popularItemQuerydslRepository = popularItemQuerydslRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    public List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics) {
        return popularItemJpaRepository.saveAll(popularItemStatistics);
    }

    @Override
    public void savePopularItemScore(PopularItem popularItem) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(POPULAR_ITEMS_KEY_PREFIX + LocalDate.now());
        zset.addScore(popularItem.getItemId(), popularItem.getOrderCount());
        zset.expire(Duration.ofDays(2));
    }

    @Override
    public List<PopularItem> findPopularItems() {
        return popularItemQuerydslRepository.findPopularItems();
    }

    @Override
    public List<PopularItem> findPopularItemScore(LocalDate date) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(POPULAR_ITEMS_KEY_PREFIX + date);
        return zset.entryRange(0, -1).stream()
                .map(e -> new PopularItem(e.getValue(), e.getScore().intValue()))
                .toList();
    }
}
