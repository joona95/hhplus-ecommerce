package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponIssueToken;
import kr.hhplus.be.server.domain.coupon.CouponIssueTokenRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class CouponIssueTokenRepositoryImpl implements CouponIssueTokenRepository {

    private final CouponCacheRepository couponCacheRepository;

    public CouponIssueTokenRepositoryImpl(CouponCacheRepository couponCacheRepository) {
        this.couponCacheRepository = couponCacheRepository;
    }

    @Override
    public void enqueueIssueToken(CouponIssueToken couponIssueToken) {
        couponCacheRepository.enqueueIssueToken(couponIssueToken);
    }

    @Override
    public List<Long> popIssueTokenUserIds(Coupon coupon, int batchSize) {
        return couponCacheRepository.popIssueTokenUserIds(coupon, batchSize);
    }

    @Override
    public void removeIssueToken(CouponIssueToken couponIssueToken) {
        couponCacheRepository.removeIssueToken(couponIssueToken);
    }

    @Override
    public long getTokenRank(CouponIssueToken couponIssueToken) {
        return couponCacheRepository.getTokenRank(couponIssueToken) + 1;
    }

    @Override
    public void enqueuePendingCouponId(long couponId) {
        couponCacheRepository.enqueuePendingCouponId(couponId);
    }

    @Override
    public Set<Long> popPendingCouponIds(int batchSize) {
        return couponCacheRepository.popPendingCouponIds(batchSize);
    }

    @Override
    public boolean hasIssueTokens(Coupon coupon) {
        return couponCacheRepository.hasIssueTokens(coupon);
    }

    @Override
    public boolean isAlreadyIssued(CouponIssueToken couponIssueToken) {
        return couponCacheRepository.isAlreadyIssued(couponIssueToken);
    }

    @Override
    public void saveCouponIssuedUser(CouponIssueToken couponIssueToken) {
        couponCacheRepository.saveCouponIssuedUser(couponIssueToken);
    }

    @Override
    public long countCouponIssuedUser(CouponIssueToken couponIssueToken) {
        return couponCacheRepository.countCouponIssuedUser(couponIssueToken);
    }
}
