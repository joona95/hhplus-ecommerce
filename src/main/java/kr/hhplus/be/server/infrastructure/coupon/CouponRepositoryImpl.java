package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.CouponIssueToken;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private static final String COUPON_STOCK_KEY_PREFIX = "coupon-stock:";
    private static final String COUPON_ISSUE_TOKEN_KEY_PREFIX = "coupon-issue-token:";
    private static final String COUPON_ISSUE_PENDING_KEY = "coupon-issue-pending";

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final RedissonClient redissonClient;

    public CouponRepositoryImpl(CouponJpaRepository couponJpaRepository, CouponIssueJpaRepository couponIssueJpaRepository, RedissonClient redissonClient) {
        this.couponJpaRepository = couponJpaRepository;
        this.couponIssueJpaRepository = couponIssueJpaRepository;
        this.redissonClient = redissonClient;
    }

    @Override
    public List<CouponIssue> findCouponIssueByUser(User user) {
        return couponIssueJpaRepository.findByUserId(user.getId());
    }

    @Override
    public Optional<CouponIssue> findCouponIssueByUserAndCouponId(User user, long couponId) {
        return couponIssueJpaRepository.findByUserIdAndCouponId(user.getId(), couponId);
    }

    @Override
    public boolean existsCouponIssueByUserAndCouponId(User user, long couponId) {
        return couponIssueJpaRepository.existsByUserIdAndCouponId(user.getId(), couponId);
    }

    @Override
    public Optional<Coupon> findCouponByIdWithLock(long couponId) {
        return couponJpaRepository.findByIdWithLock(couponId);
    }

    @Override
    public CouponIssue saveCouponIssue(CouponIssue couponIssue) {
        return couponIssueJpaRepository.save(couponIssue);
    }

    @Override
    public Optional<Coupon> findCouponById(long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public long getCouponStock(long couponId) {

        RAtomicLong rAtomicLong = redissonClient.getAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId);

        return rAtomicLong.get();
    }

    @Override
    public long countCouponIssueToken(long couponId) {
        return redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + couponId).size();
    }

    @Override
    public void saveIssueToken(CouponIssueToken couponIssueToken) {
        RScoredSortedSet<Long> zset = redissonClient.getScoredSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + couponIssueToken.couponId());
        zset.addIfAbsent(couponIssueToken.requestTime(), couponIssueToken.userId());
    }

    @Override
    public void savePendingIssueCoupon(long couponId) {
        RSet<Long> rSet = redissonClient.getSet(COUPON_ISSUE_PENDING_KEY, LongCodec.INSTANCE);
        rSet.add(couponId);
    }
}
