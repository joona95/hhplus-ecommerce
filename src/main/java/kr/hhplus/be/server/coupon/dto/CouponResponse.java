package kr.hhplus.be.server.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "쿠폰 정보 응답값")
public record CouponResponse(
        @Schema(description = "쿠폰식별자", example = "1")
        long couponId,
        @Schema(description = "쿠폰명", example = "쿠폰명")
        String couponName,
        @Schema(description = "만료일시", example = "2025-04-03 18:00:00")
        String expiredAt,
        @Schema(description = "사용여부", example = "false")
        Boolean isUsed
) {
}
