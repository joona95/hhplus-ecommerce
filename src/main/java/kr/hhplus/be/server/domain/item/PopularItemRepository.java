package kr.hhplus.be.server.domain.item;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PopularItemRepository {

    List<PopularItem> findPopularItems();

    List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics);

}
