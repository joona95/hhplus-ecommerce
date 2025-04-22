package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    private LocalDateTime expiredAt;

    private boolean isUsed;

    @CreatedDate
    private LocalDateTime issuedAt;

    public static CouponIssue of(User user, Coupon coupon) {
        return new CouponIssue(null, coupon.getId(), coupon.getCouponName(), coupon.getDiscountType(), coupon.getDiscountValue(), user, coupon.getValidFrom(), false, LocalDateTime.now());
    }

    public CouponIssue(Long id, long couponId, String couponName, DiscountType discountType, int discountValue, User user, LocalDateTime expiredAt, boolean isUsed, LocalDateTime issuedAt) {

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
        if (user == null) {
            throw new IllegalArgumentException("유저 정보가 필요합니다.");
        }
        if (expiredAt == null) {
            throw new IllegalArgumentException("만료 일시 정보가 필요합니다.");
        }

        this.id = id;
        this.couponId = couponId;
        this.couponName = couponName;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.user = user;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CouponIssue that = (CouponIssue) o;
        return couponId == that.couponId && discountValue == that.discountValue && isUsed == that.isUsed && Objects.equals(id, that.id) && Objects.equals(couponName, that.couponName) && discountType == that.discountType && Objects.equals(user, that.user) && Objects.equals(expiredAt, that.expiredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, couponId, couponName, discountType, discountValue, user, expiredAt, isUsed);
    }
}
