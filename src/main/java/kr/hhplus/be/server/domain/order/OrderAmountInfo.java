package kr.hhplus.be.server.domain.order;

import jakarta.persistence.Embeddable;
import kr.hhplus.be.server.domain.coupon.CouponIssue;
import lombok.Getter;

import java.util.Objects;

@Getter
@Embeddable
public class OrderAmountInfo {

    private final int totalAmount;

    private final int itemTotalAmount;

    private final int discountAmount;

    protected OrderAmountInfo() {
        this.totalAmount = 0;
        this.itemTotalAmount = 0;
        this.discountAmount = 0;
    }

    public static OrderAmountInfo of() {
        return new OrderAmountInfo();
    }

    public static OrderAmountInfo of(int totalAmount, int itemTotalAmount, int discountAmount) {
        return new OrderAmountInfo(totalAmount, itemTotalAmount, discountAmount);
    }

    public OrderAmountInfo(int totalAmount, int itemTotalAmount, int discountAmount) {

        if (totalAmount < 0) {
            throw new IllegalArgumentException("총 가격은 음수일 수 없습니다.");
        }
        if (itemTotalAmount < 0) {
            throw new IllegalArgumentException("총 상품 가격은 음수일 수 없습니다.");
        }
        if (discountAmount < 0) {
            throw new IllegalArgumentException("할인 가격은 음수일 수 없습니다.");
        }
        if (totalAmount != itemTotalAmount - discountAmount) {
            throw new IllegalArgumentException("가격 계산이 올바르지 않습니다.");
        }

        this.totalAmount = totalAmount;
        this.itemTotalAmount = itemTotalAmount;
        this.discountAmount = discountAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderAmountInfo that = (OrderAmountInfo) o;
        return totalAmount == that.totalAmount && itemTotalAmount == that.itemTotalAmount && discountAmount == that.discountAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalAmount, itemTotalAmount, discountAmount);
    }

    public OrderAmountInfo applyCoupon(CouponIssue couponIssue) {

        int discountAmount = couponIssue.applyDiscount(this.totalAmount);

        return OrderAmountInfo.of(this.totalAmount - discountAmount, this.itemTotalAmount, this.discountAmount + discountAmount);
    }
}
