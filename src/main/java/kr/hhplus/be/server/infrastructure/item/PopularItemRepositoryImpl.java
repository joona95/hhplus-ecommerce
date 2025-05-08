package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemRepository;
import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PopularItemRepositoryImpl implements PopularItemRepository {

    private final PopularItemJpaRepository popularItemJpaRepository;
    private final PopularItemQuerydslRepository popularItemQuerydslRepository;

    public PopularItemRepositoryImpl(PopularItemJpaRepository popularItemJpaRepository, PopularItemQuerydslRepository popularItemQuerydslRepository) {
        this.popularItemJpaRepository = popularItemJpaRepository;
        this.popularItemQuerydslRepository = popularItemQuerydslRepository;
    }

    @Override
    public List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics) {
        return popularItemJpaRepository.saveAll(popularItemStatistics);
    }

    @Override
    public List<PopularItem> findPopularItems() {
        return popularItemQuerydslRepository.findPopularItems();
    }

}
