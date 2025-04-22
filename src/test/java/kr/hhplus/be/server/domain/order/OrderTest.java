package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.Stock;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Nested
    class 주문_생성 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -100L, -10L, -3L, -2L, -1L})
        void 유저식별자가_음수인_경우_IllegalArgumentException_발생(long userId) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.유저식별자로_주문_생성(userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유저식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 주문_상태가_null_인_경우_IllegalArgumentException_발생(OrderStatus orderStatus) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.주문상태로_주문_생성(orderStatus))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 상태 정보가 필요합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 주문_가격_정보가_null_인_경우_IllegalArgumentException_발생(OrderAmountInfo orderAmountInfo) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.주문가격정보로_주문_생성(orderAmountInfo))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 가격 정보가 필요합니다.");
        }
    }

    @Nested
    class 주문_가격_계산 {

        @ParameterizedTest
        @NullSource
        void 주문_상품_정보가_null_인_경우_IllegalArgumentException_발생(List<OrderItem> orderItems) {

            //given
            Order order = OrderFixtures.정상_주문_생성();

            //when, then
            assertThatThrownBy(() -> order.calculateOrderAmount(orderItems))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 상품 정보가 필요합니다.");
        }

        @Test
        void 주문_상품의_가격과_주문_수량_곱을_모두_더하여_총_금액을_구한다() {
            //given
            Order order = new Order(1L, 1L, 1L, OrderStatus.COMPLETE, OrderAmountInfo.of(30000, 50000, 20000), LocalDateTime.now(), LocalDateTime.now());

            List<Item> items = List.of(
                    Item.of(1L, "상품명1", Stock.of(10), 10000),
                    Item.of(2L, "상품명2", Stock.of(10), 20000),
                    Item.of(3L, "상품명3", Stock.of(10), 30000)
            );

            List<OrderItem> orderItems = List.of(
                    OrderItem.of(order, items.get(0), 2),
                    OrderItem.of(order, items.get(1), 1),
                    OrderItem.of(order, items.get(2), 1)
            );

            //when
            order.calculateOrderAmount(orderItems);

            //then
            assertThat(order.getOrderAmountInfo()).isEqualTo(OrderAmountInfo.of(70000, 70000, 0));
        }
    }

}