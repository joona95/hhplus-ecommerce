package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponEventPublisher;
import kr.hhplus.be.server.domain.coupon.CouponIssueRequestEvent;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Service;

@Service
public class CouponFacadeService {

    private final CouponEventPublisher couponEventPublisher;

    public CouponFacadeService(CouponEventPublisher couponEventPublisher) {
        this.couponEventPublisher = couponEventPublisher;
    }

    public void requestCouponIssue(User user, CouponCommand.CouponIssueCommand command) {
        couponEventPublisher.send(new CouponIssueRequestEvent(command.couponId(), user.getId()));
    }
}
