package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.fixtures.OrderFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderItemsTest {

    @Nested
    class 주문_상품_목록_생성 {

        @ParameterizedTest
        @NullAndEmptySource
        void 주문_상품_목록이_빈_값인_경우_IllegalArgumentException_발생(List<OrderItem> orderItems) {

            //when, then
            assertThatThrownBy(() -> new OrderItems(orderItems))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 항목은 최소 1개 이상이어야 합니다.");
        }
    }

    @Nested
    class 주문_상품_목록_가격_계산 {

        @Test
        void 주문_상품_목록으로_주문_가격_총합_계산() {

            //given
            OrderItems orderItems = new OrderItems(List.of(
                    OrderFixtures.상품가격과_주문수량으로_주문상품_생성(20000, 1),
                    OrderFixtures.상품가격과_주문수량으로_주문상품_생성(10000, 2)
            ));

            //when
            OrderAmountInfo result = orderItems.calculateOrderAmount();

            //then
            assertThat(result).isEqualTo(OrderAmountInfo.of(40000, 40000, 0));
        }
    }
}