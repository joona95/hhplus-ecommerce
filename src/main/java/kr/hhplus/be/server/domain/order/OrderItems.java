package kr.hhplus.be.server.domain.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record OrderItems(List<OrderItem> orderItems) implements Serializable {

    public OrderItems(List<OrderItem> orderItems) {

        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("주문 항목은 최소 1개 이상이어야 합니다.");
        }

        this.orderItems = new ArrayList<>(orderItems);
    }

    public OrderAmountInfo calculateOrderAmount() {

        int itemTotalAmount = orderItems.stream()
                .mapToInt(OrderItem::getOrderItemPrice)
                .sum();
        int discountAmount = 0;
        int totalAmount = itemTotalAmount - discountAmount;

        return OrderAmountInfo.of(totalAmount, itemTotalAmount, discountAmount);
    }

    @Override
    public List<OrderItem> orderItems() {
        return Collections.unmodifiableList(orderItems);
    }
}
