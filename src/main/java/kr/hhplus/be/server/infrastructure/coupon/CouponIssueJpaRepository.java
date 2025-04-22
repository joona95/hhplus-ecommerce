package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssue;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {

    List<CouponIssue> findByUser(User user);

    Optional<CouponIssue> findByUserAndCouponId(User user, long couponId);

    boolean existsByUserAndCouponId(User user, long couponId);
}
