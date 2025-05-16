package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.application.coupon.CouponFacadeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CouponScheduler {

    private final CouponFacadeService couponFacadeService;

    public CouponScheduler(CouponFacadeService couponFacadeService) {
        this.couponFacadeService = couponFacadeService;
    }

    @Scheduled(fixedRate = 1000L)
    public void processCouponIssue() {
        couponFacadeService.processCouponIssue();
    }
}

