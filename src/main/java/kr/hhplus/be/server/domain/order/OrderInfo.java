package kr.hhplus.be.server.domain.order;

import java.util.List;

public record OrderInfo(
        Order order,
        List<OrderItem> orderItems
) {

    public static OrderInfo of(Order order, List<OrderItem> orderItems) {
        return new OrderInfo(order, orderItems);
    }

    public int getTotalAmount() {
        return order.getOrderAmountInfo().getTotalAmount();
    }

    public void applyDiscount(int discountAmount) {
        order.applyDiscount(discountAmount);
    }
}
