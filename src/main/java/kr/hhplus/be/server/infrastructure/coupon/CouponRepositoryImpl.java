package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;

    public CouponRepositoryImpl(CouponJpaRepository couponJpaRepository, CouponIssueJpaRepository couponIssueJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
        this.couponIssueJpaRepository = couponIssueJpaRepository;
    }

    @Override
    public List<CouponIssue> findByUserId(long userId) {
        return couponIssueJpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<CouponIssue> findByUserIdAndCouponId(long userId, long couponId) {
        return couponIssueJpaRepository.findByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public boolean existsCouponIssueByUserIdAndCouponId(long userId, long couponId) {
        return couponIssueJpaRepository.existsByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public Optional<Coupon> findCouponById(long couponId) {
        return couponJpaRepository.findById(couponId);
    }

    @Override
    public CouponIssue saveCouponIssue(CouponIssue couponIssue) {
        return couponIssueJpaRepository.save(couponIssue);
    }
}
