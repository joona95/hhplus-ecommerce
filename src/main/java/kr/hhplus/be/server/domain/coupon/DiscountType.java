package kr.hhplus.be.server.domain.coupon;

public enum DiscountType {
    FIXED {
        @Override
        public DiscountPolicy getDiscountPolicy(int discountValue) {
            return new FixedDiscountPolicy(discountValue);
        }
    },
    RATE {
        @Override
        public DiscountPolicy getDiscountPolicy(int discountValue) {
            return new RateDiscountPolicy(discountValue);
        }
    };

    public abstract DiscountPolicy getDiscountPolicy(int discountValue);
}
