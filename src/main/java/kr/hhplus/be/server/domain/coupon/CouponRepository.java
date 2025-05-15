package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository {

    List<CouponIssue> findCouponIssueByUser(User user);

    Optional<CouponIssue> findCouponIssueByUserAndCouponId(User user, long couponId);

    boolean existsCouponIssueByUserAndCouponId(User user, long couponId);

    Optional<Coupon> findCouponByIdWithLock(long couponId);

    CouponIssue saveCouponIssue(CouponIssue couponIssue);

    Optional<Coupon> findCouponById(long couponId);

    long getCouponStock(long couponId);

    long countCouponIssueToken(long couponId);

    void saveIssueToken(CouponIssueToken couponIssueToken);

    void savePendingIssueCoupon(long couponId);
}
