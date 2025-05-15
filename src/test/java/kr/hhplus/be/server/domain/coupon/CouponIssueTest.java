package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponIssueTest {

    @Nested
    class 쿠폰_발급_생성 {

        @ParameterizedTest
        @NullAndEmptySource
        void 쿠폰명이_비어있으면_IllegalArgumentException_발생(String couponName) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.쿠폰명으로_쿠폰_발급_내역_생성(couponName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰명을 입력해주세요.");
        }

        @ParameterizedTest
        @NullSource
        void 할인타입이_null_이면_IllegalArgumentException_발생(DiscountType discountType) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.할인타입으로_쿠폰_발급_내역_생성(discountType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인 타입 정보가 필요합니다.");
        }


        @ParameterizedTest
        @ValueSource(ints = {-10000, -10, -3, -2, -1, 0})
        void 할인율_할인금액이_0이하이면_IllegalArgumentException_발생(int discountValue) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.할인율_할인금액으로_쿠폰_발급_내역_생성(discountValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("할인율/금액은 양수여야 합니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {-10000L, -10L, -3L, -2L, -1L})
        void 쿠폰식별자가_음수면_IllegalArgumentException_발생(long couponId) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.쿠폰식별자로_쿠폰_발급_내역_생성(couponId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(longs = {-10000L, -10L, -3L, -2L, -1L})
        void 유저가_음수면_IllegalArgumentException_발생(long userId) {

            //when, then
            assertThatThrownBy(() -> CouponFixtures.유저식별자로_쿠폰_발급_내역_생성(userId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유저식별자는 음수일 수 없습니다.");
        }

        @ParameterizedTest
        @NullSource
        void 만료_일시가_null_이면_IllegalArgumentException_발생(LocalDateTime expiredAt) {

            //when, then
            assertThatThrownBy(() ->  CouponFixtures.만료일자로_쿠폰_발급_내역_생성(expiredAt))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("만료 일시 정보가 필요합니다.");
        }
    }

    @Nested
    class 쿠폰_할인_적용 {

        @Test
        void 이미_사용한_쿠폰일_경우_RuntimeException_발생() {

            //given
            CouponIssue couponIssue = CouponFixtures.사용한_쿠폰_발급_내역_생성();

            //when, then
            assertThatThrownBy(() -> couponIssue.applyDiscount(100000))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("사용할 수 없는 쿠폰입니다.");
        }

        @Test
        void 만료일시가_지난_경우_RuntimeException_발생() {

            //given
            CouponIssue couponIssue = CouponFixtures.만료일자로_쿠폰_발급_내역_생성(LocalDateTime.MIN);

            //when, then
            assertThatThrownBy(() -> couponIssue.applyDiscount(100000))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("사용할 수 없는 쿠폰입니다.");
        }

        @Test
        void 할인타입이_정액인_경우_정액_할인금액_계산() {

            //given
            CouponIssue couponIssue = CouponFixtures.정액_할인_쿠폰_발급_내역_생성(10000);

            //when
            int result = couponIssue.applyDiscount(20000);

            //when, then
            assertThat(result).isEqualTo(10000);
        }

        @Test
        void 할인타입이_정률인_경우_정률_할인금액_계산() {

            //given
            CouponIssue couponIssue = CouponFixtures.정률_할인_쿠폰_발급_내역_생성(10);

            //when
            int result = couponIssue.applyDiscount(20000);

            //when, then
            assertThat(result).isEqualTo(2000);
        }
    }
}