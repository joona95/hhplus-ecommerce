package kr.hhplus.be.server.domain.order;

public interface OrderEventPublisher {

    void send(OrderCompleteEvent event);
}
