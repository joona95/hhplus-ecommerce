package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;

public class CouponCriteria {

    public record IssuedCouponCriteria(
            User user,
            long couponId
    ) {

        public static IssuedCouponCriteria of(User user, long couponId) {
            return new IssuedCouponCriteria(user, couponId);
        }
    }

}
