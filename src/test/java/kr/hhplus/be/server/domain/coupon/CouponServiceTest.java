package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.fixtures.CouponFixtures;
import kr.hhplus.be.server.fixtures.UserFixtures;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
                            CouponFixtures.유저식별자로_쿠폰_발급_내역_생성(user.getId()),
                            CouponFixtures.유저식별자로_쿠폰_발급_내역_생성(user.getId())
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
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(user.getId(), coupon.getId()))
                    .thenReturn(true);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(user.getId(), coupon))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("쿠폰을 이미 발급 받았습니다.");
        }

        @Test
        void 쿠폰_내역_저장_레포지토리_1회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(user.getId(), coupon.getId()))
                    .thenReturn(false);

            //when
            couponService.issueCoupon(user.getId(), coupon);

            //then
            verify(couponRepository, times(1)).saveCouponIssue(CouponIssue.of(user.getId(), coupon));
        }

        @Test
        void 쿠폰_발급_가능_수량_1개_감소() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);
            int stock = coupon.getCount();

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(user.getId(), coupon.getId()))
                    .thenReturn(false);

            //when
            couponService.issueCoupon(user.getId(), coupon);

            //then
            verify(couponRepository, times(1)).saveCouponStock(coupon.getId(), stock - 1);
        }

        @Test
        void 쿠폰_발급_실패_시_쿠폰_발급_내역_저장_0회_호출() {

            //given
            User user = UserFixtures.식별자로_유저_생성(1L);
            Coupon coupon = CouponFixtures.식별자로_쿠폰_생성(1L);

            when(couponRepository.existsCouponIssueByUserIdAndCouponId(user.getId(), 1L))
                    .thenReturn(true);

            //when, then
            assertThatThrownBy(() -> couponService.issueCoupon(user.getId(), coupon))
                    .isInstanceOf(RuntimeException.class);

            verify(couponRepository, times(0)).saveCouponIssue(CouponIssue.of(user.getId(), coupon));
        }
    }

    @Nested
    class 쿠폰_발급_토큰_요청 {

        @ParameterizedTest
        @ValueSource(longs = {2L, 3L, 4L})
        void 쿠폰_발급_가능_수량보다_발급량이_많은_경우_토큰_제거_후_RuntimeException_발생(long tokenCount) {

            // given
            User user = UserFixtures.식별자로_유저_생성(1L);
            CouponIssueCommand command = CouponIssueCommand.of(1L);

            when(couponRepository.getCouponStock(command.couponId()))
                    .thenReturn(1L);
            when(couponRepository.countCouponIssueToken(command.couponId()))
                    .thenReturn(tokenCount);

            // when, then
            assertThatThrownBy(() -> couponService.requestCouponIssue(user, command))
                    .isInstanceOf(RuntimeException.class)
                            .hasMessageContaining("쿠폰 발급 가능한 수량을 초과하였습니다.");

            verify(couponRepository, times(1)).removeIssueToken(any());
            verify(couponRepository, times(0)).savePendingIssueCoupon(command.couponId());
        }

        @ParameterizedTest
        @ValueSource(longs = {1L, 2L, 3L})
        void 쿠폰_발급_가능_수량보다_발급량이_작거나_같은_경우_토큰_등록_및_발급_대기_쿠폰식별자_등록(long tokenCount) {

            // given
            User user = UserFixtures.식별자로_유저_생성(1L);
            CouponIssueCommand command = CouponIssueCommand.of(1L);

            when(couponRepository.getCouponStock(command.couponId()))
                    .thenReturn(3L);
            when(couponRepository.countCouponIssueToken(command.couponId()))
                    .thenReturn(tokenCount);

            //when
            couponService.requestCouponIssue(user, command);

            // when, then
            verify(couponRepository, times(1)).saveIssueToken(any());
            verify(couponRepository, times(0)).removeIssueToken(any());
            verify(couponRepository, times(1)).savePendingIssueCoupon(command.couponId());
        }
    }
}