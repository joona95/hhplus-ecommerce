package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateDiscountPolicyTest {

    @Nested
    class 정률_할인_정책_생성 {

        @ParameterizedTest
        @ValueSource(ints = {-10000, -100, -3, -2, -1, 101, 102, 103, 200})
        void 할인금액이_0미만이거나_100초과면_IllegalArgumentException_발생(int discountRate) {

            //when, then
            assertThatThrownBy(() -> new RateDiscountPolicy(discountRate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인율은 0 이상 100 이하여야 합니다.");
        }
    }

    @Nested
    class 정률_할인_금액_계산 {

        @Test
        void 총금액에서_할인율_적용하여_계산() {

            //given
            DiscountPolicy discountPolicy = new RateDiscountPolicy(10);

            //when
            int result = discountPolicy.calculateDiscount(10000);

            //then
            assertThat(result).isEqualTo(1000);
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