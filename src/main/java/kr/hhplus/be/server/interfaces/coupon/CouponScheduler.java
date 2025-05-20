package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CouponScheduler {

    private final CouponService couponService;

    public CouponScheduler(CouponService couponService) {
        this.couponService = couponService;
    }

    @Scheduled(fixedRate = 1000L)
    public void processCouponIssues() {
        couponService.issuePendingCoupons();
    }
}

