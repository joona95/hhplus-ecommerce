package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long couponId;

    private String couponName;

    @Enumerated
    private DiscountType discountType;

    private int discountValue;

    private long userId;

    private LocalDateTime expiredAt;

    private boolean isUsed;

    @CreatedDate
    private LocalDateTime issuedAt;

    public CouponIssue(Long id, long couponId, String couponName, DiscountType discountType, int discountValue, long userId, LocalDateTime expiredAt, boolean isUsed, LocalDateTime issuedAt) {

        if (couponId < 0) {
            throw new IllegalArgumentException("쿠폰식별자는 음수일 수 없습니다.");
        }
        if (!StringUtils.hasText(couponName)) {
            throw new IllegalArgumentException("쿠폰명을 입력해주세요.");
        }
        if (discountType == null) {
            throw new IllegalArgumentException("할인 타입 정보가 필요합니다.");
        }
        if (discountValue <= 0) {
            throw new IllegalArgumentException("할인율/금액은 양수여야 합니다.");
        }
        if (userId < 0) {
            throw new IllegalArgumentException("유저식별자는 음수일 수 없습니다.");
        }
        if (expiredAt == null) {
            throw new IllegalArgumentException("만료 일시 정보가 필요합니다.");
        }

        this.id = id;
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.userId = userId;
        this.expiredAt = expiredAt;
        this.isUsed = isUsed;
        this.issuedAt = issuedAt;
    }

    public int applyDiscount(int totalAmount) {

        if (isUsed || expiredAt.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("사용할 수 없는 쿠폰입니다.");
        }
        this.isUsed = true;
        DiscountPolicy discountPolicy = discountType.getDiscountPolicy(discountValue);
        return discountPolicy.calculateDiscount(totalAmount);
    }
}
