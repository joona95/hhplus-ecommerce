package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItem;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PopularItemRedissonRepository implements PopularItemCacheRepository {

    private static final String POPULAR_ITEMS_KEY_PREFIX = "popular-items:";

    private final RedissonClient redissonClient;

    public PopularItemRedissonRepository(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void savePopularItemScore(PopularItem popularItem) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(POPULAR_ITEMS_KEY_PREFIX + LocalDate.now());
        zset.addScore(popularItem.getItemId(), popularItem.getOrderCount());
    }

    @Override
    public List<PopularItem> findPopularItemScore(LocalDate date) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(POPULAR_ITEMS_KEY_PREFIX + date);
        return zset.entryRange(0, -1).stream()
                .map(e -> new PopularItem(e.getValue(), e.getScore().intValue()))
                .toList();
    }
}
