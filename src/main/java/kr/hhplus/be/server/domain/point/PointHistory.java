package kr.hhplus.be.server.domain.point;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long pointId;

    private Long orderId;

    private Amount amount;

    @Enumerated
    TransactionType type;

    @CreatedDate
    LocalDateTime createdAt;

    public static PointHistory ofCharge(long pointId, int amount) {
        return PointHistory.ofCharge(null, pointId, Amount.of(amount));
    }

    public static PointHistory ofCharge(Long id, long pointId, Amount amount) {
        return new PointHistory(id, pointId, null, amount, TransactionType.CHARGE, LocalDateTime.now());
    }

    public static PointHistory ofUse(long pointId, long orderId, int amount) {
        return PointHistory.ofUse(null, pointId, orderId, Amount.of(amount));
    }

    public static PointHistory ofUse(Long id, long pointId, long orderId, Amount amount) {
        return new PointHistory(id, pointId, orderId, amount, TransactionType.USE, LocalDateTime.now());
    }

    public PointHistory(Long id, long pointId, Long orderId, Amount amount, TransactionType type, LocalDateTime createdAt) {

        if (pointId < 0) {
            throw new IllegalArgumentException("포인트식별자는 음수일 수 없습니다.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("금액 정보가 필요합니다.");
        }
        if (type == null) {
            throw new IllegalArgumentException("거래 타입 정보가 필요합니다.");
        }

        this.id = id;
        this.pointId = pointId;
        this.orderId = orderId;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointHistory that = (PointHistory) o;
        return pointId == that.pointId && Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId) && Objects.equals(amount, that.amount) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pointId, orderId, amount, type);
    }
}
