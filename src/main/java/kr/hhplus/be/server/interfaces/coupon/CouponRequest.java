package kr.hhplus.be.server.interfaces.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.coupon.CouponCommand;

public class CouponRequest {

    @Schema(title = "쿠폰 발급 요청값")
    public record CouponIssueRequest(
            @Schema(description = "쿠폰식별자", example = "1")
            @Positive
            long couponId
    ) {

        public CouponCommand.CouponIssueCommand toCommand() {
            return CouponCommand.CouponIssueCommand.of(couponId);
        }
    }
}