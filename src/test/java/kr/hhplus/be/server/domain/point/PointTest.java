package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.PointFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class PointTest {

    @Nested
    class 잔액_생성 {

        @ParameterizedTest
        @NullSource
        void 유저가_null_이면_IllegalArgumentException_발생(User user) {

            //when, then
            assertThatThrownBy(() -> PointFixtures.유저로_잔액_생성(user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유저 정보가 필요합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 금액이_null_이면_IllegalArgumentException_발생(Amount amount) {

            //when, then
            assertThatThrownBy(() -> PointFixtures.금액으로_잔액_생성(amount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잔액 정보가 필요합니다.");
        }
    }

    @Nested
    class 잔액_충전 {

        @ParameterizedTest
        @ValueSource(ints = {500001, 500002, 500003, 1000000})
        void 잔액에_충전금액을_더한_값이_최대_한도_초과면_IllegalArgumentException_발생(int value) {

            //given
            Point point = PointFixtures.금액으로_잔액_생성(500000);

            //when, then
            assertThatThrownBy(() -> point.charge(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최대 한도를 초과하여 충전할 수 없습니다.");
        }
    }

    @Nested
    class 잔액_사용 {

        @ParameterizedTest
        @ValueSource(ints = {500001, 500002, 500003, 1000000})
        void 잔액에_사용금액을_뺀_값이_음수면_IllegalArgumentException_발생(int value) {

            //given
            Point point = PointFixtures.금액으로_잔액_생성(500000);

            //when, then
            assertThatThrownBy(() -> point.use(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잔액이 부족합니다.");
        }
    }
}