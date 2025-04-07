package kr.hhplus.be.server.domain.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class PopularItemTest {

    @Nested
    class 인기_상품_생성 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -100L, -10L, -3L, -2L, -1L})
        void 상품식별자가_음수일_경우_IllegalArgumentException_발생(long itemId) {

            //when, then
            assertThatThrownBy(() -> PopularItem.of(1L, itemId, LocalDate.now(), 100))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 주문날짜가_null_인_경우_IllegalArgumentException_발생(LocalDate orderDate) {

            //when, then
            assertThatThrownBy(() -> PopularItem.of(1L, 1L, orderDate, 100))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문날짜 정보가 필요합니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1})
        void 주문수량이_음수일_경우_IllegalArgumentException_발생(int orderCount) {

            //when, then
            assertThatThrownBy(() -> PopularItem.of(1L, 1L, LocalDate.now(), orderCount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 수량은 음수일 수 없습니다.");
        }
    }
}