package kr.hhplus.be.server.domain.item;

import kr.hhplus.be.server.fixtures.ItemFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class PopularItemStatisticsTest {

    @Nested
    class 인기_상품_생성 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -100L, -10L, -3L, -2L, -1L})
        void 상품식별자가_음수일_경우_IllegalArgumentException_발생(long itemId) {

            //when, then
            assertThatThrownBy(() -> ItemFixtures.상품식별자로_인기_상품_통계_생성(itemId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 주문날짜가_null_인_경우_IllegalArgumentException_발생(LocalDate orderDate) {

            //when, then
            assertThatThrownBy(() -> ItemFixtures.주문날짜로_인기_상품_통계_생성(orderDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문날짜 정보가 필요합니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1})
        void 주문수량이_음수일_경우_IllegalArgumentException_발생(int orderCount) {

            //when, then
            assertThatThrownBy(() -> ItemFixtures.주문수량으로_인기_상품_통계_생성(orderCount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("주문 수량은 음수일 수 없습니다.");
        }
    }
}