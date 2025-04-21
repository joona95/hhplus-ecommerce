package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.Stock;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.UserFixtures;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.domain.order.OrderCommand.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;
    @InjectMocks
    OrderService orderService;

    OrderCreateCommand command;
    Order order;
    List<OrderItem> orderItems;

    @BeforeEach
    void setUp() {

        User user = UserFixtures.식별자로_유저_생성(1L);
        List<Item> items = List.of(
                Item.of(1L, "상품명1", Stock.of(10), 10000),
                Item.of(2L, "상품명2", Stock.of(10), 20000)
        );

        List<OrderItemCreateCommand> itemCommands = List.of(
                OrderItemCreateCommand.of(items.get(0), 1),
                OrderItemCreateCommand.of(items.get(1), 2)
        );

        command = OrderCreateCommand.of(user, itemCommands);

        order = new Order(1L, user, 1L, OrderStatus.COMPLETE, OrderAmountInfo.of(30000, 50000, 20000), LocalDateTime.now(), LocalDateTime.now());

        orderItems = List.of(
                OrderItem.of(order, items.get(0), 1),
                OrderItem.of(order, items.get(1), 2));
    }

    @Nested
    class 주문_생성 {

        @Test
        void 주문_저장_레포지토리_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(orderRepository.saveOrder(Order.of(user)))
                    .thenReturn(order);

            when(orderRepository.saveOrderItems(orderItems))
                    .thenReturn(orderItems);

            //when
            orderService.createOrder(command);

            //then
            verify(orderRepository, times(1)).saveOrder(Order.of(user));
        }

        @Test
        void 주문_상품_목록_저장_레포지토리_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(orderRepository.saveOrder(Order.of(user)))
                    .thenReturn(order);

            when(orderRepository.saveOrderItems(orderItems))
                    .thenReturn(orderItems);

            //when
            orderService.createOrder(command);

            //then
            verify(orderRepository, times(1)).saveOrderItems(orderItems);
        }

        @Test
        void 주문_저장_실패_시_주문_상품_목록_저장_레포지토리_0회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(orderRepository.saveOrder(Order.of(user)))
                    .thenThrow(RuntimeException.class);

            //when, then
            assertThatThrownBy(() -> orderService.createOrder(command))
                    .isInstanceOf(RuntimeException.class);

            verify(orderRepository, times(0)).saveOrderItems(orderItems);
        }
    }
}