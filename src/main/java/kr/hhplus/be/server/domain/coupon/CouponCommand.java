package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.order.Order;

public class CouponCommand {

    public record CouponApplyCommand(
            Order order,
            long couponId
    ) {

        public static CouponApplyCommand of(Order order, long couponId) {
            return new CouponApplyCommand(order, couponId);
        }
    }

    public record CouponIssueCommand(
            long couponId
    ) {
        public static CouponIssueCommand of(long couponId) {
            return new CouponIssueCommand(couponId);
        }
    }
}
