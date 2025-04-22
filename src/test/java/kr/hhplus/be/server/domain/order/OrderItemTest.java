package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.fixtures.ItemFixtures;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class OrderItemTest {

    @Nested
    class 주문_상품_생성 {

        @ParameterizedTest
        @NullSource
        void 주문정보가_null_인_경우_IllegalArgumentException_발생(Order order) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.주문으로_주문상품_생성(order))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 정보가 필요합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -100L, -10L, -3L, -2L, -1L})
        void 상품식별자가_음수인_경우_IllegalArgumentException_발생(long itemId) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.상품식별자로_주문상품_생성(itemId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 상품명이_비어있는_경우_IllegalArgumentException_발생(String itemName) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.상품명으로_주문상품_생성(itemName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품명을 입력해주세요.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1})
        void 상품_가격이_음수인_경우_IllegalArgumentException_발생(int sellPrice) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.상품가격으로_주문상품_생성(sellPrice))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 가격은 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1, 0})
        void 주문수량이_양수가_아닌_경우_IllegalArgumentException_발생(int count) {

            //when, then
            assertThatThrownBy(() -> OrderFixtures.주문수량으로_주문상품_생성(count))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 수량은 양수여야 합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 주문_정보가_null_인_경우_IllegalArgumentException_발생(Order order) {

            //given
            Item item = ItemFixtures.식별자로_상품_생성(1L);

            //when, then
            assertThatThrownBy(() -> OrderItem.of(order, item, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 정보가 필요합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 상품_정보가_null_인_경우_IllegalArgumentException_발생(Item item) {

            //given
            Order order = OrderFixtures.정상_주문_생성();

            //when, then
            assertThatThrownBy(() -> OrderItem.of(order, item, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 정보가 필요합니다.");
        }
    }

    @Nested
    class 주문_가격_계산 {

        @Test
        void 주문_상품_가격과_주문_수량을_곱한_값을_전달() {

            //given
            OrderItem orderItem = OrderFixtures.정상_주문상품_생성();

            //when
            int result = orderItem.getOrderItemPrice();

            //then
            assertThat(result).isEqualTo(50000);
        }
    }
}