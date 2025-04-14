package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Nested;
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
            assertThatThrownBy(() -> new Coupon(1L, couponName, DiscountType.FIXED, 10000, LocalDateTime.MIN, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰명을 입력해주세요.");
        }

        @ParameterizedTest
        @NullSource
        void 할인타입이_null_이면_IllegalArgumentException_발생(DiscountType discountType) {

            //when, then
            assertThatThrownBy(() -> new Coupon(1L, "쿠폰명", discountType, 10000, LocalDateTime.MIN, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인 타입 정보가 필요합니다.");
        }

        @ParameterizedTest
        @NullSource
        void 유효한_시작일시_null_이면_IllegalArgumentException_발생(LocalDateTime validTo) {

            //when, then
            assertThatThrownBy(() -> new Coupon(1L, "쿠폰명", DiscountType.FIXED, 10000, validTo, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 유효 기간을 입력해주세요.");
        }


        @ParameterizedTest
        @NullSource
        void 유효한_종료일시_null_이면_IllegalArgumentException_발생(LocalDateTime validFrom) {

            //when, then
            assertThatThrownBy(() -> new Coupon(1L, "쿠폰명", DiscountType.FIXED, 10000, LocalDateTime.MIN, validFrom, 100, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 유효 기간을 입력해주세요.");
        }

        @ParameterizedTest
        @ValueSource(ints = {-10000, -10, -3, -2, -1, 0})
        void 할인율_할인금액이_0이하이면_IllegalArgumentException_발생(int discountValue) {

            //when, then
            assertThatThrownBy(() -> new Coupon(1L, "쿠폰명", DiscountType.FIXED, discountValue, LocalDateTime.MIN, LocalDateTime.MAX, 100, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인율/금액은 양수여야 합니다.");
        }


        @ParameterizedTest
        @ValueSource(ints = {-10000, -10, -3, -2, -1, 0})
        void 수량이_0이하이면_IllegalArgumentException_발생(int count) {

            //when, then
            assertThatThrownBy(() -> new Coupon(1L, "쿠폰명", DiscountType.FIXED, 10000, LocalDateTime.MIN, LocalDateTime.MAX, count, LocalDateTime.now(), LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 수량은 양수여야 합니다.");
        }
    }
}