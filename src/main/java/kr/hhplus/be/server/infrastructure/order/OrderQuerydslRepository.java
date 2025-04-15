package kr.hhplus.be.server.infrastructure.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.QOrder;
import kr.hhplus.be.server.domain.order.QOrderItem;
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

    public List<OrderItem> findTodayOrderItems() {
        return queryFactory.selectFrom(orderItem)
                .innerJoin(order).on(order.id.eq(orderItem.orderId))
                .where(order.createdAt.between(LocalDateTime.of(LocalDate.now(), LocalTime.MIN), LocalDateTime.of(LocalDate.now(), LocalTime.MAX)))
                .fetch();
    }
}
