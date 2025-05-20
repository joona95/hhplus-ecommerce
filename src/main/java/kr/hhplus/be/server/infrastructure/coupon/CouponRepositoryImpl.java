package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;

    private final CouponCacheRepository couponCacheRepository;

    public CouponRepositoryImpl(CouponJpaRepository couponJpaRepository, CouponIssueJpaRepository couponIssueJpaRepository, CouponCacheRepository couponCacheRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.couponIssueJpaRepository = couponIssueJpaRepository;
        this.couponCacheRepository = couponCacheRepository;
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
    public List<Coupon> findCouponsByIdIn(Collection<Long> couponIds) {
        return couponJpaRepository.findAllById(couponIds);
    }

    @Override
    public long getCouponStock(long couponId) {
        if (couponCacheRepository.hasCouponStock(couponId)) {
            return couponCacheRepository.getCouponStock(couponId);
        }
        Coupon coupon = couponJpaRepository.findById(couponId).orElse(null);
        return coupon == null ? 0 : coupon.getCount();
    }

    @Override
    public void saveCouponStock(long couponId, int count) {
        couponCacheRepository.saveCouponStock(couponId, count);
    }
}
