package kr.hhplus.be.server.domain.coupon;

public interface CouponEventPublisher {

    void send(CouponIssueRequestEvent event);
}
