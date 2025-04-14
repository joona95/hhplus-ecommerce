package kr.hhplus.be.server.domain.coupon;

public class RateDiscountPolicy implements DiscountPolicy {

    private final int discountRate;

    public RateDiscountPolicy(int discountRate) {

        if (discountRate < 0 || discountRate > 100) {
            throw new IllegalArgumentException("할인율은 0 이상 100 이하여야 합니다.");
        }

        this.discountRate = discountRate;
    }

    @Override
    public int calculateDiscount(int totalAmount) {
        return totalAmount * discountRate / 100;
    }
}
