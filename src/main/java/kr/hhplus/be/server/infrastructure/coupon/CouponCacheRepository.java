package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssueToken;

import java.util.List;
import java.util.Set;

public interface CouponCacheRepository {

    boolean hasCouponStock(long couponId);

    long getCouponStock(long couponId);

    void saveCouponStock(long couponId, int count);

    void enqueueIssueToken(CouponIssueToken couponIssueToken);

    List<Long> popIssueTokenUserIds(Coupon coupon, int size);

    void removeIssueToken(CouponIssueToken couponIssueToken);

    long getTokenRank(CouponIssueToken couponIssueToken);

    void enqueuePendingCouponId(long couponId);

    Set<Long> popPendingCouponIds(int batchSize);

    boolean hasIssueTokens(Coupon coupon);

    boolean isAlreadyIssued(CouponIssueToken couponIssueToken);

    void saveCouponIssuedUser(CouponIssueToken couponIssueToken);
}
