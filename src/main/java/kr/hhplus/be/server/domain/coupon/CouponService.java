package kr.hhplus.be.server.domain.coupon;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static kr.hhplus.be.server.domain.coupon.CouponCriteria.*;

@Service
public class CouponService {

    private static final int BATCH_SIZE = 500;

    private final CouponRepository couponRepository;
    private final CouponIssueTokenRepository couponIssueTokenRepository;

    public CouponService(CouponRepository couponRepository, CouponIssueTokenRepository couponIssueTokenRepository) {
        this.couponRepository = couponRepository;
        this.couponIssueTokenRepository = couponIssueTokenRepository;
    }

    public List<CouponIssue> findByUser(User user) {
        return couponRepository.findCouponIssueByUser(user);
    }

    public CouponIssue findIssuedCoupon(IssuedCouponCriteria command) {
        return couponRepository.findCouponIssueByUserAndCouponId(command.user(), command.couponId())
                .orElseThrow(() -> new RuntimeException("해당 쿠폰을 보유하고 있지 않습니다."));
    }

    public void requestCouponIssue(User user, CouponCommand.CouponIssueCommand command) {

        CouponIssueToken couponIssueToken = CouponIssueToken.of(user, command.couponId());
        couponIssueTokenRepository.enqueueIssueToken(couponIssueToken);

        if (couponIssueTokenRepository.isAlreadyIssued(couponIssueToken)) {
            throw new RuntimeException("이미 쿠폰 발급 요청한 유저입니다.");
        }

        long couponStock = couponRepository.getCouponStock(command.couponId());
        long issueTokenRank = couponIssueTokenRepository.getTokenRank(couponIssueToken);
        if (couponStock < issueTokenRank) {
            couponIssueTokenRepository.removeIssueToken(couponIssueToken);
            throw new RuntimeException("쿠폰 발급 가능한 수량을 초과하였습니다.");
        }

        couponIssueTokenRepository.enqueuePendingCouponId(command.couponId());
        couponIssueTokenRepository.saveCouponIssuedUser(couponIssueToken);
    }

    @Transactional
    public void issuePendingCoupons() {

        Set<Long> pendingCouponIds = couponIssueTokenRepository.popPendingCouponIds(BATCH_SIZE);
        List<Coupon> pendingIssueCoupons = couponRepository.findCouponsByIdIn(pendingCouponIds);
        for (Coupon coupon : pendingIssueCoupons) {

            List<Long> couponIssueUserIds = couponIssueTokenRepository.popIssueTokenUserIds(coupon, BATCH_SIZE);
            for (Long userId : couponIssueUserIds) {

                coupon.issue();

                couponRepository.saveCouponIssue(CouponIssue.of(userId, coupon));
                couponRepository.saveCouponStock(coupon.getId(), coupon.getCount());

                if (couponIssueTokenRepository.hasIssueTokens(coupon)) {
                    couponIssueTokenRepository.enqueuePendingCouponId(coupon.getId());
                }
            }
        }
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
}
