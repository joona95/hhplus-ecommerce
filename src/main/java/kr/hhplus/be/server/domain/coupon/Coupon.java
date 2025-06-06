package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private int discountValue;

    private LocalDateTime validTo;

    private LocalDateTime validFrom;

    private int count;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Coupon(Long id, String couponName, DiscountType discountType, int discountValue, LocalDateTime validTo, LocalDateTime validFrom, int count, LocalDateTime createdAt, LocalDateTime updatedAt) {

        if (!StringUtils.hasText(couponName)) {
            throw new IllegalArgumentException("쿠폰명을 입력해주세요.");
        }
        if (discountType == null) {
            throw new IllegalArgumentException("할인 타입 정보가 필요합니다.");
        }
        if (discountValue <= 0) {
            throw new IllegalArgumentException("할인율/금액은 양수여야 합니다.");
        }
        if (validTo == null || validFrom == null) {
            throw new IllegalArgumentException("쿠폰 유효 기간을 입력해주세요.");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("쿠폰 수량은 양수여야 합니다.");
        }

        this.id = id;
        this.couponName = couponName;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.validTo = validTo;
        this.validFrom = validFrom;
        this.count = count;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void issue() {

        LocalDateTime now = LocalDateTime.now();
        if (validTo.isAfter(now) || validFrom.isBefore(now)) {
            throw new RuntimeException("유효하지 않은 쿠폰입니다.");
        }
        if (count <= 0) {
            throw new RuntimeException("선착순 쿠폰 발급이 이미 종료되었습니다.");
        }

        this.count--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coupon coupon = (Coupon) o;
        return discountValue == coupon.discountValue && count == coupon.count && Objects.equals(id, coupon.id) && Objects.equals(couponName, coupon.couponName) && discountType == coupon.discountType && Objects.equals(validTo, coupon.validTo) && Objects.equals(validFrom, coupon.validFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, couponName, discountType, discountValue, validTo, validFrom, count);
    }
}
