package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;

import java.util.Objects;

public record CouponIssueToken(
        long userId,
        long couponId,
        long requestTime
) {
    public static CouponIssueToken of(User user, long couponId) {
        return CouponIssueToken.of(user.getId(), couponId);
    }

    public static CouponIssueToken of(long userId, long couponId) {
        return new CouponIssueToken(userId, couponId, System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponIssueToken that = (CouponIssueToken) o;
        return userId == that.userId && couponId == that.couponId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, couponId);
    }
}
