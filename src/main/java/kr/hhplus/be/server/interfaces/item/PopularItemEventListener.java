package kr.hhplus.be.server.interfaces.item;

import kr.hhplus.be.server.domain.item.PopularItemService;
import kr.hhplus.be.server.domain.order.OrderCompleteEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class PopularItemEventListener {

    private final PopularItemService popularItemService;

    public PopularItemEventListener(PopularItemService popularItemService) {
        this.popularItemService = popularItemService;
    }

    @KafkaListener(topics = "order-complete", groupId = "popular-item", concurrency = "3")
    public void handleOrderCompleteEvent(OrderCompleteEvent event) {

        try {
            event.getOrderItems()
                    .forEach((orderItem) -> popularItemService.savePopularItemScore(orderItem.getItemId(), orderItem.getCount()));
            log.info("오늘 주문 판매량 집계 완료");
        } catch (Exception e) {
            log.error("오늘 주문 판매량 집계 실패");
        }
    }
}
