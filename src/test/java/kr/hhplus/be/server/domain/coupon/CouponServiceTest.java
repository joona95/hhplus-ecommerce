package kr.hhplus.be.server.domain.coupon;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

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
}