package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    @Enumerated
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
}
