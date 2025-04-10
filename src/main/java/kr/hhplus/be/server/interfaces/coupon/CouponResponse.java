package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.CouponIssue;

public class CouponResponse {

    @Schema(title = "유저 쿠폰 정보 응답값")
    public record UserCouponResponse(
            @Schema(description = "쿠폰식별자", example = "1")
            long couponId,
            @Schema(description = "쿠폰명", example = "쿠폰명")
            String couponName,
            @Schema(description = "만료일시", example = "2025-04-03 18:00:00")
            String expiredAt,
            @Schema(description = "사용여부", example = "false")
            Boolean isUsed
    ) {

        public static UserCouponResponse from(CouponIssue couponIssue) {
            return new UserCouponResponse(couponIssue.getCouponId(), couponIssue.getCouponName(), couponIssue.getExpiredAt().toString(), couponIssue.isUsed());
        }
    }
}
