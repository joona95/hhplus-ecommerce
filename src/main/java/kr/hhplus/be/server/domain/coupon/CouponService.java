package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.coupon.CouponCommand.*;
import static kr.hhplus.be.server.domain.coupon.CouponCriteria.*;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<CouponIssue> findByUser(User user) {
        return couponRepository.findCouponIssueByUser(user);
    }

    public CouponIssue findIssuedCoupon(IssuedCouponCriteria command) {
        return couponRepository.findCouponIssueByUserAndCouponId(command.user(), command.couponId())
                .orElseThrow(() -> new RuntimeException("해당 쿠폰을 보유하고 있지 않습니다."));
    }

    @Transactional
    public CouponIssue issueCoupon(User user, CouponIssueCommand command) {

        if (couponRepository.existsCouponIssueByUserAndCouponId(user, command.couponId())) {
            throw new RuntimeException("쿠폰을 이미 발급 받았습니다.");
        }

        Coupon coupon = couponRepository.findCouponById(command.couponId())
                .orElseThrow(() -> new RuntimeException("쿠폰이 존재하지 않습니다."));

        coupon.issue();

        CouponIssue couponIssue = CouponIssue.of(user, coupon);

        return couponRepository.saveCouponIssue(couponIssue);
    }
}
