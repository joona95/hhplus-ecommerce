package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.OrderFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kr.hhplus.be.server.domain.coupon.CouponCommand.*;
import static kr.hhplus.be.server.domain.coupon.CouponCriteria.*;
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
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(couponRepository.findCouponIssueByUser(user))
                    .thenReturn(List.of(
                            CouponFixtures.유저로_쿠폰_발급_내역_생성(user),
                            CouponFixtures.유저로_쿠폰_발급_내역_생성(user)
                    ));

            //when
            couponService.findByUser(user);

            //then
            verify(couponRepository, times(1)).findCouponIssueByUser(user);
        }
    }

    @Nested
    class 발급_쿠폰_내역_조회 {

        @Test
        void 유저와_쿠폰식별자로_발급_쿠폰_내역_조회_레포지토리_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(couponRepository.findCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(Optional.of(CouponFixtures.정상_쿠폰_발급_내역_생성()));

            IssuedCouponCriteria criteria = IssuedCouponCriteria.of(user, 1L);

            //when
            couponService.findIssuedCoupon(criteria);

            //then
            verify(couponRepository, times(1)).findCouponIssueByUserAndCouponId(user, 1L);
        }

        @Test
        void 발급_쿠폰_내역_존재하지_않을_경우_RuntimeException_발생() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(couponRepository.findCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(Optional.empty());

            IssuedCouponCriteria criteria = IssuedCouponCriteria.of(user, 1L);

            //when, then
            assertThatThrownBy(() -> couponService.findIssuedCoupon(criteria))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("해당 쿠폰을 보유하고 있지 않습니다.");
        }
    }

    @Nested
    class 쿠폰_발급 {

        @Test
        void 이미_쿠폰_발급_받은_경우_RuntimeException_발생() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(couponRepository.existsCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(true);

            CouponIssueCommand command = CouponIssueCommand.of(1L);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(user, command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("쿠폰을 이미 발급 받았습니다.");
        }

        @Test
        void 쿠폰이_존재하지_않는_경우_RuntimeException_발생() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);

            when(couponRepository.existsCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.empty());

            CouponIssueCommand command = CouponIssueCommand.of(1L);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(user, command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("쿠폰이 존재하지 않습니다.");
        }

        @Test
        void 유저_쿠폰_보유_여부_조회_레포지토리_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.of(coupon));
            when(couponRepository.saveCouponIssue(CouponIssue.of(user, coupon)))
                    .thenReturn(CouponIssue.of(user, coupon));

            CouponIssueCommand command = CouponIssueCommand.of(1L);

            //when
            couponService.issueCoupon(user, command);

            //then
            verify(couponRepository, times(1)).existsCouponIssueByUserAndCouponId(user, 1L);
        }

        @Test
        void 쿠폰_조회_레포지토리_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.of(coupon));
            when(couponRepository.saveCouponIssue(CouponIssue.of(user, coupon)))
                    .thenReturn(CouponIssue.of(user, coupon));

            CouponIssueCommand command = CouponIssueCommand.of(1L);

            //when
            couponService.issueCoupon(user, command);

            //then
            verify(couponRepository, times(1)).findCouponById(1L);
        }

        @Test
        void 쿠폰_내역_저장_레포지토리_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(false);
            when(couponRepository.findCouponById(1L))
                    .thenReturn(Optional.of(coupon));
            when(couponRepository.saveCouponIssue(CouponIssue.of(user, coupon)))
                    .thenReturn(CouponIssue.of(user, coupon));

            CouponIssueCommand command = CouponIssueCommand.of(1L);

            //when
            couponService.issueCoupon(user, command);

            //then
            verify(couponRepository, times(1)).saveCouponIssue(CouponIssue.of(user, coupon));
        }

        @Test
        void 쿠폰_발급_실패_시_쿠폰_발급_내역_저장_0회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserAndCouponId(user, 1L))
                    .thenReturn(true);

            CouponIssueCommand command = CouponIssueCommand.of(1L);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(user, command))
                    .isInstanceOf(RuntimeException.class);

            verify(couponRepository, times(0)).saveCouponIssue(CouponIssue.of(user, coupon));
        }
    }
}