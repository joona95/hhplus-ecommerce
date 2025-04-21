package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.fixtures.PointFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class PointHistoryTest {

    @Nested
    class 포인트_내역_생성 {

        @ParameterizedTest
        @ValueSource(longs = {-1000L, -100L, -10L, -3L, -2L, -1L})
        void 포인트식별자가_음수면_IllegalArgumentException_발생(long pointId) {

            //when, then
            assertThatThrownBy(() -> PointFixtures.포인트식별자로_포인트_내역_생성(pointId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("포인트식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 금액이_null_이면_IllegalArgumentException_발생(Amount amount) {

            //when, then
            assertThatThrownBy(() -> PointFixtures.금액으로_포인트_내역_생성(amount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("금액 정보가 필요합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 거래_타입이_null_이면_IllegalArgumentException_발생(TransactionType type) {

            //when, then
            assertThatThrownBy(() -> PointFixtures.거래_타입으로_포인트_내역_생성(type))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("거래 타입 정보가 필요합니다.");
        }
    }

    @Nested
    class 충전_포인트_내역_생성 {

        @Test
        void 거래_타입이_CHARGE_이면_정상_생성() {

            //given
            Point point = PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000);

            //when
            PointHistory result = PointHistory.ofCharge(point, 1000);

            //then
            assertThat(result).isEqualTo(new PointHistory(null, 1L, null, Amount.of(1000), TransactionType.CHARGE, LocalDateTime.now()));
        }
    }

    @Nested
    class 사용_포인트_내역_생성 {

        @Test
        void 거래_타입이_USE_이면_정상_생성() {

            //given
            Point point = PointFixtures.식별자와_금액으로_잔액_생성(1L, 1000);
            Order order = OrderFixtures.식별자로_주문_생성(1L);

            //when
            PointHistory result = PointHistory.ofUse(point, order);

            //then
            assertThat(result).isEqualTo(new PointHistory(null, 1L, 1L, Amount.of(order.getTotalAmount()), TransactionType.USE, LocalDateTime.now()));
        }
    }
}