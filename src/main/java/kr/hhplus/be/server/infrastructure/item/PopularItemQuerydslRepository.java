package kr.hhplus.be.server.infrastructure.item;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.item.PopularItem;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static kr.hhplus.be.server.domain.item.QPopularItemStatistics.*;

@Repository
public class PopularItemQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    public PopularItemQuerydslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<PopularItem> findPopularItems() {

        LocalDate today = LocalDate.now();

        return queryFactory
                .select(Projections.constructor(PopularItem.class,
                        popularItemStatistics.itemId,
                        popularItemStatistics.orderCount.sum()))
                .from(popularItemStatistics)
                .where(popularItemStatistics.orderDate.between(today.minusDays(3), today.minusDays(1)))
                .groupBy(popularItemStatistics.itemId)
                .orderBy(popularItemStatistics.orderCount.sum().desc())
                .limit(5)
                .fetch();
    }
}
