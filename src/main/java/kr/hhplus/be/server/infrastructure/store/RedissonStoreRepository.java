package kr.hhplus.be.server.infrastructure.store;

import kr.hhplus.be.server.domain.item.PopularItem;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public class RedissonStoreRepository implements RedisStoreRepository {

    private final RedissonClient redissonClient;

    public RedissonStoreRepository(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public long getSortedSetSize(String key) {
        return redissonClient.getScoredSortedSet(key).size();
    }

    @Override
    public void addInSortedSetIfAbsent(String key, long value, double score) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(key);
        zset.addIfAbsent(score, value);
    }

    @Override
    public List<Long> popSortedSetBatch(String key, int size) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(key);
        return zset.pollFirst(size).stream().toList();
    }

    @Override
    public void addInSet(String key, long value) {
        RSet<Long> rSet = redissonClient.getSet(key, LongCodec.INSTANCE);
        rSet.add(value);
    }

    @Override
    public Set<Long> popSetAll(String key) {
        RSet<Long> rSet = redissonClient.getSet(key, LongCodec.INSTANCE);
        return rSet.removeRandom(Integer.MAX_VALUE);
    }

    @Override
    public long getAtomicLong(String key) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(key);
        return rAtomicLong.get();
    }

    @Override
    public void setAtomicLong(String key, long value) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(key);
        rAtomicLong.set(value);
    }

    @Override
    public void addScoreInSoredSet(String key, long value, double score) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(key);
        zset.addScore(value, score);
    }

    @Override
    public List<ScoredEntry<Long>> getSoredSet(String key) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(key);
        return zset.entryRange(0, -1).stream().toList();
    }
}
