package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.client.DataPlatformClient;
import kr.hhplus.be.server.domain.order.OrderCompleteEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderEventListener {

    private final DataPlatformClient dataPlatformClient;

    public OrderEventListener(DataPlatformClient dataPlatformClient) {
        this.dataPlatformClient = dataPlatformClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleteEvent(OrderCompleteEvent event) {
        try {
            dataPlatformClient.sendOrderData(event.orderInfo());
            log.info("주문 정보 데이터 플랫폼 전송 성공");
        } catch (Exception e) {
            log.error("주문 정보 데이터 플랫폼 전송 실패");
        }
    }
}
