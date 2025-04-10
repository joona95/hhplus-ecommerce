package kr.hhplus.be.server.domain.coupon;

public interface DiscountPolicy {

    int calculateDiscount(int totalAmount);
}
