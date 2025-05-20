package kr.hhplus.be.server.domain.item;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PopularItemRepository {

    List<PopularItem> findPopularItemScore(LocalDate date);

    List<PopularItem> findPopularItems();

    List<PopularItemStatistics> savePopularItems(List<PopularItemStatistics> popularItemStatistics);

    void savePopularItemScore(PopularItem popularItem);
}
