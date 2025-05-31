package kr.hhplus.be.server.domain.coupon;

public record CouponIssueRequestEvent(
        long couponId,
        long userId
) {

}
