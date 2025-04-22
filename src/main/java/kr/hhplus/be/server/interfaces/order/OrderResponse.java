package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.OrderResult;

import java.util.List;

public class OrderResponse {
    @Schema(title = "주문 결제 응답값")
    public record OrderDetailResponse(
            @Schema(description = "주문식별자", example = "1")
            long orderId,
            @Schema(description = "주문상태", example = "COMPLETE")
            String orderStatus,
            @Schema(description = "쿠폰식별자", example = "1")
            Long couponId,
            @Schema(description = "주문 상품 정보 목록", example = "[]")
            List<OrderItemResponse> orderItems,
            @Schema(description = "주문 금액", example = "10000")
            long totalAmount,
            @Schema(description = "총 상품 금액", example = "15000")
            long itemTotalAmount,
            @Schema(description = "할인 금액", example = "5000")
            long discountAmount,
            @Schema(description = "주문일시", example = "2025-04-01 18:00:00")
            String createdAt
    ) {
        public static OrderDetailResponse from(OrderResult.OrderCreateResult result) {

            List<OrderItemResponse> orderItems = result.orderItems().stream()
                    .map(OrderItemResponse::from)
                    .toList();

            return new OrderDetailResponse(
                    result.orderId(),
                    result.orderStatus().name(),
                    result.couponId(),
                    orderItems,
                    result.totalAmount(),
                    result.itemTotalAmount(),
                    result.discountAmount(),
                    result.createdAt().toString()
            );
        }
    }

    @Schema(title = "주문 상품 응답값")
    public record OrderItemResponse(
            @Schema(description = "주문상품식별자", example = "1")
            long orderItemId,
            @Schema(description = "주문상품명", example = "주문상품명")
            String orderItemName,
            @Schema(description = "판매가", example = "10000")
            long sellPrice,
            @Schema(description = "주문 수량", example = "1")
            int count
    ) {

        public static OrderItemResponse from(OrderResult.OrderItemCreateResult result) {
            return new OrderItemResponse(
                    result.orderItemId(),
                    result.orderItemName(),
                    result.sellPrice(),
                    result.count()
            );
        }
    }
}
