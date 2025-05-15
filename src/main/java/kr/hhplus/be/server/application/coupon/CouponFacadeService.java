package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponFacadeService {

    public final CouponService couponService;

    public CouponFacadeService(CouponService couponService) {
        this.couponService = couponService;
    }

    public void processCouponIssue() {

        List<Coupon> pendingIssueCoupons = couponService.getPendingCoupons();
        for (Coupon coupon : pendingIssueCoupons) {

            List<Long> couponIssueUserIds = couponService.popCouponIssueUserIds(coupon);
            for (Long userId : couponIssueUserIds) {
                couponService.issueCoupon(userId, coupon);
            }
        }
    }
}