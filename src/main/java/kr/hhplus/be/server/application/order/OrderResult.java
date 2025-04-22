package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResult {

    public record OrderCreateResult(
            long orderId,
            OrderStatus orderStatus,
            Long couponId,
            List<OrderItemCreateResult> orderItems,
            long totalAmount,
            long itemTotalAmount,
            long discountAmount,
            LocalDateTime createdAt
    ) {

        public static OrderCreateResult from(OrderInfo orderInfo) {

            List<OrderItemCreateResult> itemResults = orderInfo.getOrderItems().stream()
                    .map(OrderItemCreateResult::from)
                    .toList();

            Order order = orderInfo.order();

            return new OrderCreateResult(
                    order.getId(),
                    order.getOrderStatus(),
                    order.getCouponIssueId(),
                    itemResults,
                    order.getOrderAmountInfo().getTotalAmount(),
                    order.getOrderAmountInfo().getItemTotalAmount(),
                    order.getOrderAmountInfo().getDiscountAmount(),
                    order.getCreatedAt()
            );
        }
    }

    public record OrderItemCreateResult(
            long orderItemId,
            String orderItemName,
            long sellPrice,
            int count
    ) {

        public static OrderItemCreateResult from(OrderItem orderItem) {
            return new OrderItemCreateResult(orderItem.getId(), orderItem.getItemName(), orderItem.getSellPrice(), orderItem.getCount());
        }
    }
}
