package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository {

    List<CouponIssue> findCouponIssueByUser(User user);

    Optional<CouponIssue> findCouponIssueByUserAndCouponId(User user, long couponId);

    boolean existsCouponIssueByUserIdAndCouponId(long userId, long couponId);

    Optional<Coupon> findCouponByIdWithLock(long couponId);

    CouponIssue saveCouponIssue(CouponIssue couponIssue);

    long getCouponStock(long couponId);

    List<Coupon> findCouponsByIdIn(Collection<Long> pendingCouponIds);

    void saveCouponStock(long couponId, int count);
}
