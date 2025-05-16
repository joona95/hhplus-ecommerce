package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.CouponIssueToken;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infrastructure.store.RedisStoreRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private static final String COUPON_STOCK_KEY_PREFIX = "coupon-stock:";
    private static final String COUPON_ISSUE_TOKEN_KEY_PREFIX = "coupon-issue-token:";
    private static final String COUPON_ISSUE_PENDING_KEY = "coupon-issue-pending";

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final RedisStoreRepository redisStoreRepository;

    public CouponRepositoryImpl(CouponJpaRepository couponJpaRepository, CouponIssueJpaRepository couponIssueJpaRepository, RedisStoreRepository redisStoreRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.couponIssueJpaRepository = couponIssueJpaRepository;
        this.redisStoreRepository = redisStoreRepository;
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
    public boolean existsCouponIssueByUserIdAndCouponId(long userId, long couponId) {
        return couponIssueJpaRepository.existsByUserIdAndCouponId(userId, couponId);
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
        return redisStoreRepository.getAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId);
    }

    @Override
    public long countCouponIssueToken(long couponId) {
        return redisStoreRepository.getSortedSetSize(COUPON_ISSUE_TOKEN_KEY_PREFIX + couponId);
    }

    @Override
    public void saveIssueToken(CouponIssueToken couponIssueToken) {
        redisStoreRepository.addInSortedSetIfAbsent(
                COUPON_ISSUE_TOKEN_KEY_PREFIX + couponIssueToken.couponId(),
                couponIssueToken.userId(),
                couponIssueToken.requestTime());
    }

    @Override
    public void savePendingIssueCoupon(long couponId) {
        redisStoreRepository.addInSet(COUPON_ISSUE_PENDING_KEY, couponId);
    }

    @Override
    public Set<Long> getPendingIssueCouponIds() {
        return redisStoreRepository.popSetAll(COUPON_ISSUE_PENDING_KEY);
    }

    @Override
    public List<Coupon> findCouponsByIdIn(Collection<Long> couponIds) {
        return couponJpaRepository.findAllById(couponIds);
    }

    @Override
    public List<Long> popCouponIssueUserIds(Coupon coupon, int size) {
        return redisStoreRepository.popSortedSetBatch(COUPON_ISSUE_TOKEN_KEY_PREFIX + coupon.getId(), size);
    }

    @Override
    public void saveCouponStock(long couponId, int count) {
        redisStoreRepository.setAtomicLong(COUPON_STOCK_KEY_PREFIX + couponId, count);
    }

    @Override
    public void removeIssueToken(CouponIssueToken couponIssueToken) {
        redisStoreRepository.removeInSortedSet(COUPON_ISSUE_TOKEN_KEY_PREFIX + couponIssueToken.couponId(), couponIssueToken.userId());
    }
}
