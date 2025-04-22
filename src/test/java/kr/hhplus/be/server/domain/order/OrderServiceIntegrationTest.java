package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.infrastructure.order.OrderItemJpaRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @BeforeEach
    void setUp() {
        orderItemJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
    }

    @Test
    void 주문_생성_후_주문_항목_저장() {

        // given
        OrderCommand.OrderCreateCommand command = new OrderCommand.OrderCreateCommand(1L, List.of(
                new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자로_상품_생성(1L), 2),
                new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자로_상품_생성(2L), 1)
        ));

        // when
        OrderInfo orderInfo = orderService.createOrder(command);

        // then
        assertThat(orderJpaRepository.findAll().get(0)).isEqualTo(orderInfo.order());
        assertThat(orderItemJpaRepository.findAll()).isEqualTo(orderInfo.orderItems());
    }

    @Test
    void 주문_생성시_총액_계산() {

        // given
        OrderCommand.OrderCreateCommand command = new OrderCommand.OrderCreateCommand(1L, List.of(
                new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자와_가격으로_상품_생성(1L, 10000), 2),
                new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자와_가격으로_상품_생성(2L, 20000), 1)
        ));

        // when
        OrderInfo orderInfo = orderService.createOrder(command);

        // then
        assertThat(orderInfo.order().getOrderAmountInfo()).isEqualTo(OrderAmountInfo.of(40000, 40000, 0));
    }
}
