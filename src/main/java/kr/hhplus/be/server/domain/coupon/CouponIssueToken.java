package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;

public record CouponIssueToken(
        long userId,
        long couponId,
        long requestTime
) {
    public static CouponIssueToken of(User user, long couponId) {
        return new CouponIssueToken(user.getId(), couponId, System.currentTimeMillis());
    }
}
