package kr.hhplus.be.server.interfaces.item;

import kr.hhplus.be.server.domain.item.ItemEvent;
import kr.hhplus.be.server.domain.item.PopularItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class ItemEventHandler {

    private final PopularItemService popularItemService;

    public ItemEventHandler(PopularItemService popularItemService) {
        this.popularItemService = popularItemService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ItemEvent.StockDecreasedEvent event) {
        log.info("오늘 주문 판매량 집계");
        popularItemService.savePopularItemScore(event.itemId(), event.count());
    }
}
