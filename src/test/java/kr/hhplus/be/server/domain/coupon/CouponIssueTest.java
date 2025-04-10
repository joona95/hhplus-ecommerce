package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponIssueTest {

    @Nested
    class 쿠폰_발급_생성 {

        @ParameterizedTest
        @NullAndEmptySource
        void 쿠폰명이_비어있으면_IllegalArgumentException_발생(String couponName) {

            //when, then
            assertThatThrownBy(() -> new CouponIssue(1L, 1L, couponName, DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰명을 입력해주세요.");
        }

        @ParameterizedTest
        @NullSource
        void 할인타입이_null_이면_IllegalArgumentException_발생(DiscountType discountType) {

            //when, then
            assertThatThrownBy(() ->  new CouponIssue(1L, 1L, "쿠폰명", discountType, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인 타입 정보가 필요합니다.");
        }


        @ParameterizedTest
        @ValueSource(ints = {-10000, -10, -3, -2, -1, 0})
        void 할인율_할인금액이_0이하이면_IllegalArgumentException_발생(int discountValue) {

            //when, then
            assertThatThrownBy(() ->  new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, discountValue, 1L, LocalDateTime.MAX, false, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인율/금액은 양수여야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {-10000L, -10L, -3L, -2L, -1L})
        void 쿠폰식별자가_음수면_IllegalArgumentException_발생(long couponId) {

            //when, then
            assertThatThrownBy(() -> new CouponIssue(1L, couponId, "쿠폰명", DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {-10000L, -10L, -3L, -2L, -1L})
        void 유저식별자가_음수면_IllegalArgumentException_발생(long userId) {

            //when, then
            assertThatThrownBy(() -> new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, 10000, userId, LocalDateTime.MAX, false, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유저식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 만료_일시가_null_이면_IllegalArgumentException_발생(LocalDateTime expiredAt) {

            //when, then
            assertThatThrownBy(() ->  new CouponIssue(1L, 1L, "쿠폰명", DiscountType.FIXED, 10000, 1L, expiredAt, false, LocalDateTime.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("만료 일시 정보가 필요합니다.");
        }
    }
}