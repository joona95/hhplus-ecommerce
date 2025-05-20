package kr.hhplus.be.server.domain.item;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

    @CachePut(value = "cache:popular-items", key = "'cache:popular-items'")
    public List<PopularItem> warmupPopularItems() {
        return Optional.ofNullable(popularItemRepository.findPopularItems()).orElse(List.of());
    }

    @Transactional
    public void createPopularItems() {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<PopularItem> popularItems = popularItemRepository.findPopularItemScore(yesterday);

        List<PopularItemStatistics> popularItemStatistics = popularItems.stream()
                .map(popularItem -> PopularItemStatistics.of(popularItem.getItemId(), yesterday, popularItem.getOrderCount()))
                .toList();

        popularItemRepository.savePopularItems(popularItemStatistics);
    }

    public void savePopularItemScore(Long itemId, int count) {
        popularItemRepository.savePopularItemScore(new PopularItem(itemId, count));
    }
}
