package kr.hhplus.be.server.infrastructure.item;

import kr.hhplus.be.server.domain.item.PopularItem;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PopularItemCacheRepository {

    void savePopularItemScore(PopularItem popularItem);

    List<PopularItem> findPopularItemScore(LocalDate date);
}
