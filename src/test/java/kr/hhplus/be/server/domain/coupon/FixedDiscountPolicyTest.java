package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class FixedDiscountPolicyTest {

    @Nested
    class 정액_할인_정책_생성 {

        @ParameterizedTest
        @ValueSource(ints = {-10000, -100, -3, -2, -1})
        void 할인금액이_음수면_IllegalArgumentException_발생(int discountAmount) {

            //when, then
            assertThatThrownBy(() -> new FixedDiscountPolicy(discountAmount))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인 금액은 음수일 수 없습니다.");
        }
    }

    @Nested
    class 정액_할인_금액_계산 {

        @Test
        void 총금액이_할인금액보다_작은_경우_총금액이_할인금액으로_계산() {

            //given
            DiscountPolicy discountPolicy = new FixedDiscountPolicy(20000);

            //when
            int result = discountPolicy.calculateDiscount(10000);

            //then
            assertThat(result).isEqualTo(10000);
        }

        @Test
        void 할인금액이_총금액보다_작은_경우_할인금액만큼_계산() {

            //given
            DiscountPolicy discountPolicy = new FixedDiscountPolicy(10000);

            //when
            int result = discountPolicy.calculateDiscount(20000);

            //then
            assertThat(result).isEqualTo(10000);
        }
    }
}