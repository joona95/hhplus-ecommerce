package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "주문 결제 응답값")
public record OrderResponse(
        @Schema(description = "주문식별자", example = "1")
        long orderId,
        @Schema(description = "주문상태", example = "COMPLETE")
        String orderStatus,
        @Schema(description = "쿠폰식별자", example = "1")
        long couponId,
        @Schema(description = "주문 상품 정보 목록", example = "[]")
        List<OrderItemResponse> orderItems,
        @Schema(description = "주문 금액", example = "10000")
        long totalAmount,
        @Schema(description = "할인 금액", example = "5000")
        long discountAmout,
        @Schema(description = "주문일시", example = "2025-04-01 18:00:00")
        String createdAt
) {
}
