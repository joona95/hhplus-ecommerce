package kr.hhplus.be.server.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "상품 상세 정보 응답값")
public record ItemResponse(
        @Schema(description = "상품식별자", example = "1")
        long id,
        @Schema(description = "상품명", example = "상품명")
        String itemName,
        @Schema(description = "상품 가격", example = "1000")
        long price,
        @Schema(description = "상품 재고", example = "100")
        long stock
) {
}
