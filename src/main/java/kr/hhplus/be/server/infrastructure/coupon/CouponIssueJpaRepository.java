package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {

    List<CouponIssue> findByUserId(long userId);

    Optional<CouponIssue> findByUserIdAndCouponId(long userId, long couponId);

    boolean existsByUserIdAndCouponId(long userId, long couponId);
}
