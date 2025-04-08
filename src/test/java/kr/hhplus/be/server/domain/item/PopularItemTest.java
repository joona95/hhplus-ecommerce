package kr.hhplus.be.server.domain.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PopularItemTest {

    @Nested
    class 인기_상품_생성 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -100L, -10L, -3L, -2L, -1L})
        void 상품식별자가_음수일_경우_IllegalArgumentException_발생(long itemId) {

            //when, then
            assertThatThrownBy(() -> new PopularItem(1L, itemId, "상품명", 1000, LocalDate.now(), 100, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 주문날짜가_null_인_경우_IllegalArgumentException_발생(LocalDate orderDate) {

            //when, then
            assertThatThrownBy(() -> new PopularItem(1L, 1L, "상품명", 1000, orderDate, 100, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문날짜 정보가 필요합니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1})
        void 주문수량이_음수일_경우_IllegalArgumentException_발생(int orderCount) {

            //when, then
            assertThatThrownBy(() -> new PopularItem(1L, 1L, "상품명", 1000, LocalDate.now(), orderCount, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 수량은 음수일 수 없습니다.");
        }
    }
}