package kr.hhplus.be.server.domain.item;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class StockTest {

    @Nested
    class 재고_생성 {

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1})
        void 재고가_음수이면_IllegalArgumentException_발생(int count) {

            //when, then
            assertThatThrownBy(() -> Stock.of(count))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고는 음수가 될 수 없습니다.");
        }
    }

    @Nested
    class 재고_감소 {

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1})
        void 재고_차감하려는_값이_음수면_IllegalArgumentException_발생(int count) {

            //given
            Stock stock = Stock.of(100);

            //when, then
            assertThatThrownBy(() -> stock.decrease(count))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 차감하려는 값은 0 이상이어야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {1000, 105, 101})
        void 차감한_재고가_음수이면_IllegalArgumentException_발생(int count) {

            //given
            Stock stock = Stock.of(100);

            //when, then
            assertThatThrownBy(() -> stock.decrease(count))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고는 음수가 될 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(ints = {100, 10, 1, 0})
        void 차감한_재고가_0이상이면_정상_차감(int count) {

            //given
            Stock stock = Stock.of(100);

            //when
            Stock result = stock.decrease(count);

            //when, then
            assertThat(result).isEqualTo(Stock.of(100 - count));
        }
    }

    @Nested
    class 재고_증가 {

        @ParameterizedTest
        @ValueSource(ints = {-1000, -100, -10, -3, -2, -1})
        void 재고_증가하려는_값이_음수면_IllegalArgumentException_발생(int count) {

            //given
            Stock stock = Stock.of(100);

            //when, then
            assertThatThrownBy(() -> stock.increase(count))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("재고 증가하려는 값은 0 이상이어야 합니다.");
        }
    }
}