package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CouponRepository {

    List<CouponIssue> findCouponIssueByUser(User user);

    Optional<CouponIssue> findCouponIssueByUserAndCouponId(User user, long couponId);

    boolean existsCouponIssueByUserIdAndCouponId(long userId, long couponId);

    Optional<Coupon> findCouponByIdWithLock(long couponId);

    CouponIssue saveCouponIssue(CouponIssue couponIssue);

    Optional<Coupon> findCouponById(long couponId);

    long getCouponStock(long couponId);

    long countCouponIssueToken(long couponId);

    void saveIssueToken(CouponIssueToken couponIssueToken);

    void savePendingIssueCoupon(long couponId);

    Set<Long> popPendingIssueCouponIds();

    List<Coupon> findCouponsByIdIn(Collection<Long> pendingCouponIds);

    List<Long> popCouponIssueUserIds(Coupon coupon, int size);

    void saveCouponStock(long couponId, int count);

    void removeIssueToken(CouponIssueToken couponIssueToken);
}
