package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponIssueRequestEvent;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CouponEventListenerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponEventListener couponEventListener;

    @Test
    void 쿠폰_발급_요청_이벤트_발행_시_쿠폰_발급() {

        //given
        long userId = 1L;
        long couponId = 1L;
        CouponIssueRequestEvent event = new CouponIssueRequestEvent(couponId, userId);

        //when
        couponEventListener.handleCouponIssueRequestEvent(event);

        //then
        verify(couponService, times(1)).handleCouponIssueRequest(userId, couponId);
    }
}
