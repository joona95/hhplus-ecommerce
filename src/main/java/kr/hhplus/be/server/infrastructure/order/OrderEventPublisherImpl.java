package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.OrderCompleteEvent;
import kr.hhplus.be.server.domain.order.OrderEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisherImpl implements OrderEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderEventPublisherImpl(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void send(OrderCompleteEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
