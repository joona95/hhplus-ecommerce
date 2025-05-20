package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CouponIssueTokenRepository {

    void enqueueIssueToken(CouponIssueToken couponIssueToken);

    List<Long> popIssueTokenUserIds(Coupon coupon, int batchSize);

    void removeIssueToken(CouponIssueToken couponIssueToken);

    long getTokenRank(CouponIssueToken couponIssueToken);

    void enqueuePendingCouponId(long couponId);

    Set<Long> popPendingCouponIds(int batchSize);

    boolean hasIssueTokens(Coupon coupon);

    boolean isAlreadyIssued(CouponIssueToken couponIssueToken);

    void saveCouponIssuedUser(CouponIssueToken couponIssueToken);
}
