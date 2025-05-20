package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemRepository;
import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class PopularItemRepositoryImpl implements PopularItemRepository {

    private final PopularItemStatisticsJpaRepository popularItemStatisticsJpaRepository;
    private final PopularItemQuerydslRepository popularItemQuerydslRepository;
    private final PopularItemCacheRepository popularItemCacheRepository;

    public PopularItemRepositoryImpl(PopularItemStatisticsJpaRepository popularItemStatisticsJpaRepository, PopularItemQuerydslRepository popularItemQuerydslRepository, PopularItemCacheRepository popularItemCacheRepository) {
        this.popularItemStatisticsJpaRepository = popularItemStatisticsJpaRepository;
        this.popularItemQuerydslRepository = popularItemQuerydslRepository;
        this.popularItemCacheRepository = popularItemCacheRepository;
    }

    @Override
    public List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics) {
        return popularItemStatisticsJpaRepository.saveAll(popularItemStatistics);
    }

    @Override
    public void savePopularItemScore(PopularItem popularItem) {
        popularItemCacheRepository.savePopularItemScore(popularItem);
    }

    @Override
    public List<PopularItem> findPopularItems() {
        return popularItemQuerydslRepository.findPopularItems();
    }

    @Override
    public List<PopularItem> findPopularItemScore(LocalDate date) {
        return popularItemCacheRepository.findPopularItemScore(date);
    }
}
