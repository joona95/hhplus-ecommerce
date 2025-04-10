package kr.hhplus.be.server.application.item;

import kr.hhplus.be.server.domain.item.ItemService;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemFacadeService {

    private final ItemService itemService;
    private final OrderService orderService;

    public ItemFacadeService(ItemService itemService, OrderService orderService) {
        this.itemService = itemService;
        this.orderService = orderService;
    }

    @Transactional
    public void createOrderItemStatistics() {

        List<OrderItem> orderItems = orderService.findTodayOrderItems();

        itemService.createPopularItemStatistics(orderItems);
    }
}
