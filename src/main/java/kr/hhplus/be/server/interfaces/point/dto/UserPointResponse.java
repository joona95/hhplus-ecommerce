package kr.hhplus.be.server.interfaces.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "유저 잔액 정보 응답값")
public record UserPointResponse(
        @Schema(description = "유저식별자", example = "1")
        long userId,
        @Schema(description = "유저 잔액 정보", example = "1000")
        long amount
) {
}
