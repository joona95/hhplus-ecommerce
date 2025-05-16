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

@Service
public class ItemFacadeService {

    private final ItemService itemService;
    private final PopularItemService popularItemService;

    public ItemFacadeService(ItemService itemService, PopularItemService popularItemService) {
        this.itemService = itemService;
        this.popularItemService = popularItemService;
    }

    @Transactional
    public void createPopularItemStatistics() {

        popularItemService.createPopularItems();

        popularItemService.warmupPopularItems();
    }

    public List<PopularItemDetail> findPopularItemDetails() {
        return popularItemService.findPopularItems().stream()
                .map(popularItem -> PopularItemDetail.of(popularItem, itemService.findById(popularItem.getItemId())))  // ← 프록시 경유
                .toList();
    }
}
