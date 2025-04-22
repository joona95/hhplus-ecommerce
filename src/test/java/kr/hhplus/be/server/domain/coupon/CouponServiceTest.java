package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    CouponRepository couponRepository;
    @InjectMocks
    CouponService couponService;

    @Nested
    class 유저_보유_쿠폰_목록_조회 {

        @Test
        void 유저_쿠폰_발급_내역_조회_레포지토리_1회_호출() {

            //given
            when(couponRepository.findByUserId(1L))
                    .thenReturn(List.of(
                            CouponFixtures.유저식별자로_쿠폰_발급_내역_생성(1L),
                            CouponFixtures.유저식별자로_쿠폰_발급_내역_생성(1L)
                    ));

            //when
            couponService.findByUserId(1L);

            //then
            verify(couponRepository, times(1)).findByUserId(1L);
        }
    }

    @Nested
    class 쿠폰_할인_적용 {

        @Test
        void 유저_쿠폰_발급_조회_레포지토리_1회_호출() {

            //given
            when(couponRepository.findByUserIdAndCouponId(1L, 1L))
                    .thenReturn(Optional.of(CouponFixtures.정상_쿠폰_발급_내역_생성()));

            CouponCommand.CouponApplyCommand command = CouponCommand.CouponApplyCommand.of(OrderFixtures.정상_주문_생성(), 1L);

            //when
            couponService.applyCoupon(command);

            //then
            verify(couponRepository, times(1)).findByUserIdAndCouponId(1L, 1L);
        }

        @Test
        void 유저_쿠폰_발급_조회_실패_시_RuntimeException_발생() {

            //given
            when(couponRepository.findByUserIdAndCouponId(1L, 1L))
                    .thenReturn(Optional.empty());

            CouponCommand.CouponApplyCommand command = CouponCommand.CouponApplyCommand.of(OrderFixtures.정상_주문_생성(), 1L);

            //when, then
            assertThatThrownBy(() -> couponService.applyCoupon(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("해당 쿠폰을 보유하고 있지 않습니다.");
        }
    }

    @Nested
    class 쿠폰_발급 {

        @Test
        void 이미_쿠폰_발급_받은_경우_RuntimeException_발생() {

            //given
            when(couponRepository.existsCouponIssueByUserIdAndCouponId(1L, 1L))
                    .thenReturn(true);

            CouponCommand.CouponIssueCommand command = CouponCommand.CouponIssueCommand.of(1L, 1L);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("쿠폰을 이미 발급 받았습니다.");
        }

        @Test
        void 쿠폰이_존재하지_않는_경우_RuntimeException_발생() {

            //given
            when(couponRepository.existsCouponIssueByUserIdAndCouponId(1L, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.empty());

            CouponCommand.CouponIssueCommand command = CouponCommand.CouponIssueCommand.of(1L, 1L);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("쿠폰이 존재하지 않습니다.");
        }

        @Test
        void 유저_쿠폰_보유_여부_조회_레포지토리_1회_호출() {

            //given
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(1L, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.of(coupon));
            when(couponRepository.saveCouponIssue(CouponIssue.of(1L, coupon)))
                    .thenReturn(CouponIssue.of(1L, coupon));

            CouponCommand.CouponIssueCommand command = CouponCommand.CouponIssueCommand.of(1L, 1L);

            //when
            couponService.issueCoupon(command);

            //then
            verify(couponRepository, times(1)).existsCouponIssueByUserIdAndCouponId(1L, 1L);
        }

        @Test
        void 쿠폰_조회_레포지토리_1회_호출() {

            //given
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(1L, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.of(coupon));
            when(couponRepository.saveCouponIssue(CouponIssue.of(1L, coupon)))
                    .thenReturn(CouponIssue.of(1L, coupon));

            CouponCommand.CouponIssueCommand command = CouponCommand.CouponIssueCommand.of(1L, 1L);

            //when
            couponService.issueCoupon(command);

            //then
            verify(couponRepository, times(1)).findCouponById(1L);
        }

        @Test
        void 쿠폰_내역_저장_레포지토리_1회_호출() {

            //given
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(1L, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.of(coupon));
            when(couponRepository.saveCouponIssue(CouponIssue.of(1L, coupon)))
                    .thenReturn(CouponIssue.of(1L, coupon));

            CouponCommand.CouponIssueCommand command = CouponCommand.CouponIssueCommand.of(1L, 1L);

            //when
            couponService.issueCoupon(command);

            //then
            verify(couponRepository, times(1)).saveCouponIssue(CouponIssue.of(1L, coupon));
        }

        @Test
        void 쿠폰_발급_실패_시_쿠폰_발급_내역_저장_0회_호출() {

            //given
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(1L, 1L))
                    .thenReturn(true);

            CouponCommand.CouponIssueCommand command = CouponCommand.CouponIssueCommand.of(1L, 1L);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(command))
                    .isInstanceOf(RuntimeException.class);

            verify(couponRepository, times(0)).saveCouponIssue(CouponIssue.of(1L, coupon));
        }
    }
}