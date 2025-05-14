package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
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
    public List<CouponIssue> findCouponIssueByUser(User user) {
        return couponIssueJpaRepository.findByUser(user);
    }

    @Override
    public Optional<CouponIssue> findCouponIssueByUserAndCouponId(User user, long couponId) {
        return couponIssueJpaRepository.findByUserAndCouponId(user, couponId);
    }

    @Override
    public boolean existsCouponIssueByUserAndCouponId(User user, long couponId) {
        return couponIssueJpaRepository.existsByUserAndCouponId(user, couponId);
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
}
