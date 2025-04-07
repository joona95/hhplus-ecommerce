package kr.hhplus.be.server.interfaces.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Schema(title = "주문 결제 요청값")
public record OrderRequest(
        @Schema(description = "유저식별자", example = "1")
        @Positive
        long userId,
        @Schema(description = "상품식별자 목록", example = "[1, 2, 3]")
        @NotNull
        List<Long> itemIds
) {
}
