package kr.hhplus.be.server.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(title = "유저 잔액 충전 요청값")
public record PointChargeRequest(
        @Positive
        @Schema(description = "유저식별자", example = "1")
        long userId,
        @Positive
        @Schema(description = "충전 금액", example = "1000")
        long amount
) {
}
