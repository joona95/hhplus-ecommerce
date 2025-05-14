package kr.hhplus.be.server.interfaces.item;

import kr.hhplus.be.server.application.item.ItemFacadeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ItemScheduler {

    private final ItemFacadeService itemFacadeService;

    public ItemScheduler(ItemFacadeService itemFacadeService) {
        this.itemFacadeService = itemFacadeService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void saveOrderItemStatistics() {
        itemFacadeService.createPopularItemStatistics();
    }
}
