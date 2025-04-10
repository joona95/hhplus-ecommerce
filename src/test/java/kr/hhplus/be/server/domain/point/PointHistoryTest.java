package kr.hhplus.be.server.domain.point;

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
            assertThatThrownBy(() -> new PointHistory(1L, pointId, 1L, Amount.of(1000), TransactionType.CHARGE, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("포인트식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 금액이_null_이면_IllegalArgumentException_발생(Amount amount) {

            //when, then
            assertThatThrownBy(() -> new PointHistory(1L, 1L, 1L, amount, TransactionType.CHARGE, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("금액 정보가 필요합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 거래_타입이_null_이면_IllegalArgumentException_발생(TransactionType type) {

            //when, then
            assertThatThrownBy(() -> new PointHistory(1L, 1L, 1L, Amount.of(1000), type, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("거래 타입 정보가 필요합니다.");
        }
    }

    @Nested
    class 충전_포인트_내역_생성 {

        @Test
        void 거래_타입이_CHARGE_이면_정상_생성() {

            //when
            PointHistory result = PointHistory.ofCharge(1L, 1L, Amount.of(1000));

            //then
            assertThat(result).isEqualTo(new PointHistory(1L, 1L, null, Amount.of(1000), TransactionType.CHARGE, LocalDateTime.now()));
        }
    }

    @Nested
    class 사용_포인트_내역_생성 {

        @Test
        void 거래_타입이_USE_이면_정상_생성() {

            //when
            PointHistory result = PointHistory.ofUse(1L, 1L, 1L, Amount.of(1000));

            //then
            assertThat(result).isEqualTo(new PointHistory(1L, 1L, 1L, Amount.of(1000), TransactionType.USE, LocalDateTime.now()));
        }
    }
}