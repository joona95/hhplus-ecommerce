package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssueRequestEvent;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CouponEventListener {

    private final CouponService couponService;

    public CouponEventListener(CouponService couponService) {
        this.couponService = couponService;
    }

    @KafkaListener(topics = "coupon-issue-request", groupId = "coupon-issue", concurrency = "6")
    public void handleCouponIssueRequestEvent(CouponIssueRequestEvent event) {

        try {
            couponService.handleCouponIssueRequest(event.userId(), event.couponId());
            log.info("선착순 쿠폰 발급 완료");
        } catch (Exception e) {
            log.error("선착순 쿠폰 발급 실패");
        }
    }
}
