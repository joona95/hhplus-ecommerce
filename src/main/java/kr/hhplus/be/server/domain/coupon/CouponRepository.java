package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository {

    List<CouponIssue> findByUserId(long userId);

    Optional<CouponIssue> findByUserIdAndCouponId(long userId, long couponId);

    boolean existsCouponIssueByUserIdAndCouponId(long userId, long couponId);

    Optional<Coupon> findCouponById(long couponId);

    CouponIssue saveCouponIssue(CouponIssue couponIssue);
}
