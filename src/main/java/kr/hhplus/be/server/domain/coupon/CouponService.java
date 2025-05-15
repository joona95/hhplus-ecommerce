package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

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
    public void issueCoupon(long userId, Coupon coupon) {

        if (couponRepository.existsCouponIssueByUserIdAndCouponId(userId, coupon.getId())) {
            throw new RuntimeException("쿠폰을 이미 발급 받았습니다.");
        }

        coupon.issue();

        couponRepository.saveCouponIssue(CouponIssue.of(userId, coupon));
        couponRepository.saveCouponStock(coupon.getId(), coupon.getCount());
    }

    public void requestCouponIssue(User user, CouponCommand.CouponIssueCommand command) {

        long couponStock = couponRepository.getCouponStock(command.couponId());
        long issueTokenCount = couponRepository.countCouponIssueToken(command.couponId());

        CouponIssueToken couponIssueToken = CouponIssueToken.of(user, command.couponId());

        if (couponStock > issueTokenCount) {
            couponRepository.saveIssueToken(couponIssueToken);
            couponRepository.savePendingIssueCoupon(command.couponId());
        }
    }

    public List<Coupon> getPendingCoupons() {

        Set<Long> pendingCouponIds = couponRepository.getPendingIssueCouponIds();

        return couponRepository.findCouponsByIdIn(pendingCouponIds);
    }

    public List<Long> popCouponIssueUserIds(Coupon coupon) {
        return couponRepository.popCouponIssueUserIds(coupon, 500);
    }
}
