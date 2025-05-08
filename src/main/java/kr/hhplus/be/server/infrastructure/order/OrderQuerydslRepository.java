package kr.hhplus.be.server.infrastructure.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.OrderItem;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static kr.hhplus.be.server.domain.order.QOrder.*;
import static kr.hhplus.be.server.domain.order.QOrderItem.*;

@Repository
public class OrderQuerydslRepository {

    private final JPAQueryFactory queryFactory;

    public OrderQuerydslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public List<OrderItem> findYesterdayOrderItems() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        return queryFactory.selectFrom(orderItem)
                .innerJoin(orderItem.order, order).fetchJoin()
                .where(order.createdAt.between(LocalDateTime.of(yesterday, LocalTime.MIN), LocalDateTime.of(yesterday, LocalTime.MAX)))
                .fetch();
    }
}
