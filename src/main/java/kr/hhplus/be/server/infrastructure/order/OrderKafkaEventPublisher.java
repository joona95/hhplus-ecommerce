package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderCompleteEvent;
import kr.hhplus.be.server.domain.order.OrderEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderKafkaEventPublisher implements OrderEventPublisher {

    private static final String ORDER_COMPLETE_TOPIC = "order-complete";
    private final KafkaTemplate<String, OrderCompleteEvent> kafkaTemplate;

    public OrderKafkaEventPublisher(KafkaTemplate<String, OrderCompleteEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(OrderCompleteEvent event) {
        log.info("Produce message : " + event);
        this.kafkaTemplate.send(ORDER_COMPLETE_TOPIC, event);
    }
}
