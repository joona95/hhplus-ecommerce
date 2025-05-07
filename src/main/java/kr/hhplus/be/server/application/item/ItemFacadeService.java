package kr.hhplus.be.server.application.item;

import kr.hhplus.be.server.domain.item.ItemService;
import kr.hhplus.be.server.domain.order.OrderItemStatistics;
import kr.hhplus.be.server.domain.order.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemFacadeService {

    private final ItemService itemService;
    private final OrderService orderService;

    public ItemFacadeService(ItemService itemService, OrderService orderService) {
        this.itemService = itemService;
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createOrderItemStatistics() {

        OrderItemStatistics orderItemStatistics = orderService.findYesterdayOrderItemStatistics();

        itemService.createPopularItems(orderItemStatistics);
    }
}
