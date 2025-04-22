package kr.hhplus.be.server.domain.coupon;

public class CouponCommand {

    public record CouponIssueCommand(
            long couponId
    ) {
        public static CouponIssueCommand of(long couponId) {
            return new CouponIssueCommand(couponId);
        }
    }
}
