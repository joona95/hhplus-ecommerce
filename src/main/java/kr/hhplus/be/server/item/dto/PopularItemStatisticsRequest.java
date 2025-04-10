package kr.hhplus.be.server.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(title = "인기 상품 통계 데이터 전송 요청값")
public record PopularItemStatisticsRequest(
        @Schema(description = "주문식별자", example = "1")
        @Positive
        long orderId,
        @Schema(description = "유저식별자", example = "1")
        @Positive
        long userId,
        @Schema(description = "상품식별자", example = "1")
        @Positive
        long itemId
) {
}
