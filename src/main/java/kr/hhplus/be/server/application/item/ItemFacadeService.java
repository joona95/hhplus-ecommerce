package kr.hhplus.be.server.application.item;

import kr.hhplus.be.server.domain.item.ItemService;
import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemDetail;
import kr.hhplus.be.server.domain.item.PopularItemService;
import kr.hhplus.be.server.domain.order.OrderItemStatistics;
import kr.hhplus.be.server.domain.order.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemFacadeService {

    private final ItemService itemService;
    private final PopularItemService popularItemService;
    private final OrderService orderService;

    public ItemFacadeService(ItemService itemService, PopularItemService popularItemService, OrderService orderService) {
        this.itemService = itemService;
        this.popularItemService = popularItemService;
        this.orderService = orderService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void createOrderItemStatistics() {

        OrderItemStatistics orderItemStatistics = orderService.findYesterdayOrderItemStatistics();

        popularItemService.createPopularItems(orderItemStatistics);
    }

    public List<PopularItemDetail> findPopularItemDetails() {
        return popularItemService.findPopularItems().stream()
                .map(popularItem -> PopularItemDetail.of(popularItem, itemService.findById(popularItem.getItemId())))  // ← 프록시 경유
                .toList();
    }
}
