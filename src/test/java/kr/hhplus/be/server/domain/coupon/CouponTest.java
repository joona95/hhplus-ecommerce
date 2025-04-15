package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.fixtures.CouponFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class CouponTest {

    @Nested
    class 쿠폰_생성 {

        @ParameterizedTest
        @NullAndEmptySource
        void 쿠폰명이_비어있으면_IllegalArgumentException_발생(String couponName) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.쿠폰명으로_쿠폰_생성(couponName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰명을 입력해주세요.");
        }

        @ParameterizedTest
        @NullSource
        void 할인타입이_null_이면_IllegalArgumentException_발생(DiscountType discountType) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.할인타입으로_쿠폰_생성(discountType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인 타입 정보가 필요합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 유효한_시작일시_null_이면_IllegalArgumentException_발생(LocalDateTime validTo) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.유효시작일로_쿠폰_생성(validTo))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 유효 기간을 입력해주세요.");
        }


        @ParameterizedTest
        @NullSource
        void 유효한_종료일시_null_이면_IllegalArgumentException_발생(LocalDateTime validFrom) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.유효종료일로_쿠폰_생성(validFrom))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 유효 기간을 입력해주세요.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-10000, -10, -3, -2, -1, 0})
        void 할인율_할인금액이_0이하이면_IllegalArgumentException_발생(int discountValue) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.할인율_할인금액으로_쿠폰_생성(discountValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인율/금액은 양수여야 합니다.");
        }


        @ParameterizedTest
        @ValueSource(ints = {-10000, -10, -3, -2, -1, 0})
        void 수량이_0이하이면_IllegalArgumentException_발생(int count) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.발급수량으로_쿠폰_생성(count))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 수량은 양수여야 합니다.");
        }
    }

    @Nested
    class 쿠폰_발급 {

        @Test
        void 유효한_시작일시가_현재_이후_이면_RuntimeException_발생() {

            //given
            Coupon coupon = CouponFixtures.유효시작일로_쿠폰_생성(LocalDateTime.MAX);

            //when, then
            assertThatThrownBy(coupon::issue)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("유효하지 않은 쿠폰입니다.");
        }


        @Test
        void 유효한_종료일시가_현재_이전_이면_RuntimeException_발생() {

            //given
            Coupon coupon = CouponFixtures.유효종료일로_쿠폰_생성(LocalDateTime.MIN);

            //when, then
            assertThatThrownBy(coupon::issue)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("유효하지 않은 쿠폰입니다.");
        }

        @Test
        void 수량이_없는_경우_RuntimeException_발생() {

            //given
            Coupon coupon = CouponFixtures.발급수량으로_쿠폰_생성(1);
            coupon.issue();

            //when, then
            assertThatThrownBy(coupon::issue)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("선착순 쿠폰 발급이 이미 종료되었습니다.");
        }

        @Test
        void 정상_발급시_수량_하나_감소() {

            //given
            Coupon coupon = CouponFixtures.정상_쿠폰_생성();

            //when
            coupon.issue();

            //then
            assertThat(coupon.getCount()).isEqualTo(9);
        }
    }
}