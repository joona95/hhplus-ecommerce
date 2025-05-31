package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssueToken;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class CouponRedissonRepository implements CouponCacheRepository {

    private static final String COUPON_STOCK_KEY_PREFIX = "coupon-stock:";
    private static final String COUPON_ISSUE_TOKEN_KEY_PREFIX = "coupon-issue-token:";

    private static final String COUPON_ISSUED_PREFIX = "coupon-issued:";
    private static final String COUPON_ISSUE_PENDING_KEY = "coupon-issue-pending";

    private final RedissonClient redissonClient;

    public CouponRedissonRepository(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean hasCouponStock(long couponId) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId);
        return rAtomicLong.isExists();
    }

    @Override
    public long getCouponStock(long couponId) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId);
        return rAtomicLong.get();
    }

    @Override
    public void saveCouponStock(long couponId, int count) {
        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId);
        rAtomicLong.set(count);
    }

    @Override
    public void enqueueIssueToken(CouponIssueToken couponIssueToken) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + couponIssueToken.couponId());
        zset.addIfAbsent(couponIssueToken.requestTime(), couponIssueToken.userId());
    }

    @Override
    public List<Long> popIssueTokenUserIds(Coupon coupon, int batchSize) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + coupon.getId());
        return zset.pollFirst(batchSize).stream().toList();
    }

    @Override
    public void removeIssueToken(CouponIssueToken couponIssueToken) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + couponIssueToken.couponId());
        zset.remove(couponIssueToken.userId());
    }

    @Override
    public long getTokenRank(CouponIssueToken couponIssueToken) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + couponIssueToken.couponId());
        return zset.rank(couponIssueToken.userId());
    }

    @Override
    public void enqueuePendingCouponId(long couponId) {
        RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUE_PENDING_KEY, LongCodec.INSTANCE);
        rSet.add(couponId);
    }

    @Override
    public Set<Long> popPendingCouponIds(int batchSize) {
        RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUE_PENDING_KEY, LongCodec.INSTANCE);
        return rSet.removeRandom(batchSize);
    }

    @Override
    public boolean hasIssueTokens(Coupon coupon) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + coupon.getId());
        return !zset.isEmpty();
    }

    @Override
    public boolean isAlreadyIssued(CouponIssueToken couponIssueToken) {
        RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUED_PREFIX + couponIssueToken.couponId(), LongCodec.INSTANCE);
        return rSet.contains(couponIssueToken.userId());
    }

    @Override
    public void saveCouponIssuedUser(CouponIssueToken couponIssueToken) {
        RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUED_PREFIX + couponIssueToken.couponId(), LongCodec.INSTANCE);
        rSet.add(couponIssueToken.userId());
    }

    @Override
    public long countCouponIssuedUser(CouponIssueToken couponIssueToken) {
        RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUED_PREFIX + couponIssueToken.couponId(), LongCodec.INSTANCE);
        return rSet.size();
    }
}
