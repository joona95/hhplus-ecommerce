package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

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
}
