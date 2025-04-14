package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderAmountInfo;
import kr.hhplus.be.server.domain.order.OrderStatus;
import org.assertj.core.api.Assertions;
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
                            new CouponIssue(1L, 1L, "쿠폰명1", DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now()),
                            new CouponIssue(2L, 2L, "쿠폰명2", DiscountType.RATE, 100, 1L, LocalDateTime.MAX, false, LocalDateTime.now())
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
            CouponIssue couponIssue = new CouponIssue(1L, 1L, "쿠폰명1", DiscountType.FIXED, 10000, 1L, LocalDateTime.MAX, false, LocalDateTime.now());

            when(couponRepository.findByUserIdAndCouponId(1L, 1L))
                    .thenReturn(Optional.of(couponIssue));

            Order order = new Order(1L, 1L, 1L, OrderStatus.COMPLETE, OrderAmountInfo.of(30000, 50000, 20000), LocalDateTime.now(), LocalDateTime.now());

            CouponCommand.CouponApplyCommand command = CouponCommand.CouponApplyCommand.of(order, 1L);

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

            Order order = new Order(1L, 1L, 1L, OrderStatus.COMPLETE, OrderAmountInfo.of(30000, 50000, 20000), LocalDateTime.now(), LocalDateTime.now());

            CouponCommand.CouponApplyCommand command = CouponCommand.CouponApplyCommand.of(order, 1L);

            //when, then
            assertThatThrownBy(() -> couponService.applyCoupon(command))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("해당 쿠폰을 보유하고 있지 않습니다.");
        }
    }
}