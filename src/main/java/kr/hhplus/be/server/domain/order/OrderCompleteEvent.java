package kr.hhplus.be.server.domain.order;

import java.util.List;

public record OrderCompleteEvent(
        OrderInfo orderInfo
) {

    public List<OrderItem> getOrderItems() {
        return orderInfo.orderItems();
    }
}
