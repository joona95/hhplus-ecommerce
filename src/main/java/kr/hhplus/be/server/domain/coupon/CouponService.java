package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand.CouponApplyCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public List<CouponIssue> findByUserId(long userId) {
        return couponRepository.findByUserId(userId);
    }

    @Transactional
    public int applyCoupon(CouponApplyCommand command) {

        CouponIssue couponIssue = couponRepository.findByUserIdAndCouponId(command.getUserId(), command.couponId())
                .orElseThrow(() -> new RuntimeException("해당 쿠폰을 보유하고 있지 않습니다."));

        return couponIssue.applyDiscount(command.getTotalAmount());
    }
}
