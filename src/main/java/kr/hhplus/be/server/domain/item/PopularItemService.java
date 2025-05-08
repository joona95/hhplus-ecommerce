package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.domain.order.OrderItemStatistics;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PopularItemService {

    private final PopularItemRepository popularItemRepository;

    public PopularItemService(PopularItemRepository popularItemRepository) {
        this.popularItemRepository = popularItemRepository;
    }

    @Cacheable(value = "cache:popular-items", key = "'cache:popular-items'")
    public List<PopularItem> findPopularItems() {
        return Optional.ofNullable(popularItemRepository.findPopularItems()).orElse(List.of());
    }

    @Transactional
    public List<PopularItemStatistics> createPopularItems(OrderItemStatistics orderItemStatistics) {

        List<PopularItemStatistics> popularItemStatistics = orderItemStatistics.getItemIds().stream()
                .map(itemId -> PopularItemStatistics.of(itemId, orderItemStatistics.getOrderDate(itemId), orderItemStatistics.getTotalOrderCount(itemId)))
                .toList();

        return popularItemRepository.savePopularItems(popularItemStatistics);
    }
}
