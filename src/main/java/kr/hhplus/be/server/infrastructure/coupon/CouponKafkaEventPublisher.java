package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponEventPublisher;
import kr.hhplus.be.server.domain.coupon.CouponIssueRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CouponKafkaEventPublisher implements CouponEventPublisher {

    private static final String COUPON_ISSUE_REQUEST_TOPIC = "coupon-issue-request";
    private final KafkaTemplate<String, CouponIssueRequestEvent> kafkaTemplate;

    public CouponKafkaEventPublisher(KafkaTemplate<String, CouponIssueRequestEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(CouponIssueRequestEvent event) {
        log.info("Produce message : " + event);
        this.kafkaTemplate.send(COUPON_ISSUE_REQUEST_TOPIC, String.valueOf(event.couponId()), event);
    }
}
