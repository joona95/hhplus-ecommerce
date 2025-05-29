package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.client.DataPlatformClient;
import kr.hhplus.be.server.domain.order.OrderCompleteEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderEventListener {

    private final DataPlatformClient dataPlatformClient;

    public OrderEventListener(DataPlatformClient dataPlatformClient) {
        this.dataPlatformClient = dataPlatformClient;
    }

    @KafkaListener(topics = "order-complete", groupId = "order", concurrency = "3")
    public void handleOrderCompleteEvent(OrderCompleteEvent event) {
        try {
            dataPlatformClient.sendOrderData(event.orderInfo());
            log.info("주문 정보 데이터 플랫폼 전송 성공");
        } catch (Exception e) {
            log.error("주문 정보 데이터 플랫폼 전송 실패");
        }
    }
}
