package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class OrderRepositoryImplTest {

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private OrderRepositoryImpl orderRepository;

    @BeforeEach
    void setUp() {
        orderItemJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
    }

    @Test
    void 주문을_저장() {

        // given
        Order order = OrderFixtures.정상_주문_생성();

        // when
        Order result = orderRepository.saveOrder(order);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result).isEqualTo(order);
    }

    @Test
    void 주문상품들을_저장() {

        // given
        OrderItem item1 = OrderFixtures.정상_주문상품_생성();
        OrderItem item2 = OrderFixtures.정상_주문상품_생성();

        // when
        List<OrderItem> result = orderRepository.saveOrderItems(List.of(item1, item2));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(item1);
        assertThat(result.get(1)).isEqualTo(item2);
    }

    @Test
    void 오늘_주문의_주문상품들만_조회된다() {

        // given
        LocalDateTime today = LocalDateTime.now();
        Order todayOrder = OrderFixtures.생성일시로_주문_생성(today);
        orderJpaRepository.save(todayOrder);
        orderItemJpaRepository.saveAll(List.of(
                OrderFixtures.주문으로_주문상품_생성(todayOrder),
                OrderFixtures.주문으로_주문상품_생성(todayOrder)
        ));

        LocalDateTime yesterday = today.minusDays(1);
        Order yesterdayOrder = OrderFixtures.생성일시로_주문_생성(yesterday);
        orderJpaRepository.save(yesterdayOrder);
        orderItemJpaRepository.save(
                OrderFixtures.주문으로_주문상품_생성(yesterdayOrder)
        );

        // when
        List<OrderItem> result = orderRepository.findTodayOrderItems();

        // then
        assertThat(result).hasSize(2); // 오늘 주문 2건만 나와야 함
        assertThat(result).allMatch(orderItem ->
                Objects.equals(orderItem.getOrder().getId(), todayOrder.getId())
        );
    }
}