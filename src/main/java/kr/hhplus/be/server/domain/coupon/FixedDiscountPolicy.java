package kr.hhplus.be.server.domain.coupon;

public class FixedDiscountPolicy implements DiscountPolicy {

    private final int discountAmount;

    public FixedDiscountPolicy(int discountAmount) {

        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 금액은 음수일 수 없습니다.");
        }

        this.discountAmount = discountAmount;
    }

    @Override
    public int calculateDiscount(int totalAmount) {
        return Math.min(totalAmount, discountAmount);
    }
}
