package kr.hhplus.be.server.infrastructure.store;

import org.redisson.client.protocol.ScoredEntry;

import java.util.List;
import java.util.Set;

public interface RedisStoreRepository {

    long getSortedSetSize(String key);

    void addInSortedSetIfAbsent(String key, long value, double score);

    void addScoreInSoredSet(String key, long value, double score);

    List<ScoredEntry<Long>> getSoredSet(String key);

    List<Long> popSortedSetBatch(String key, int size);

    void removeInSortedSet(String key, long value);

    void addInSet(String key, long value);

    Set<Long> popSetAll(String key);

    long getAtomicLong(String key);

    void setAtomicLong(String key, long value);

}
