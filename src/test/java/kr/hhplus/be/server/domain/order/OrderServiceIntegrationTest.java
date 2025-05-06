package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import kr.hhplus.be.server.infrastructure.order.OrderItemJpaRepository;
import kr.hhplus.be.server.infrastructure.order.OrderJpaRepository;
import kr.hhplus.be.server.infrastructure.support.DatabaseCleanup;
import kr.hhplus.be.server.infrastructure.user.UserJpaRepository;
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

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @BeforeEach
    void setUp() {
        databaseCleanup.truncateAllTables();
    }

    @Test
    void 주문_생성_후_주문_항목_저장() {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        OrderCommand.OrderCreateCommand command = new OrderCommand.OrderCreateCommand(
                user,
                List.of(
                        new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자로_상품_생성(1L), 2),
                        new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자로_상품_생성(2L), 1)
                ));

        // when
        OrderInfo orderInfo = orderService.createOrder(command);

        // then
        assertThat(orderJpaRepository.findAll().get(0).getId()).isEqualTo(orderInfo.order().getId());
        assertThat(orderItemJpaRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    void 주문_생성시_총액_계산() {

        // given
        User user = userJpaRepository.save(UserFixtures.정상_유저_생성());
        OrderCommand.OrderCreateCommand command = new OrderCommand.OrderCreateCommand(
                user,
                List.of(
                        new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자와_가격으로_상품_생성(1L, 10000), 2),
                        new OrderCommand.OrderItemCreateCommand(ItemFixtures.식별자와_가격으로_상품_생성(2L, 20000), 1)
                ));

        // when
        OrderInfo orderInfo = orderService.createOrder(command);

        // then
        assertThat(orderInfo.order().getOrderAmountInfo()).isEqualTo(OrderAmountInfo.of(40000, 40000, 0));
    }
}
