package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.CouponIssue;

import java.util.List;

public record OrderInfo(
        Order order,
        List<OrderItem> orderItems
) {

    public static OrderInfo of(Order order, OrderItems orderItems) {
        return new OrderInfo(order, orderItems.orderItems());
    }

    public void applyCoupon(CouponIssue couponIssue) {
        order.applyCoupon(couponIssue);
    }

}
