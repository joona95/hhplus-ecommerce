package kr.hhplus.be.server.domain.item;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class PopularItemStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long itemId;

    private LocalDate orderDate;

    private int orderCount;

    @CreatedDate
    private LocalDateTime createdAt;

    public static PopularItemStatistics of(long itemId, LocalDate orderDate, int orderCount) {
        return new PopularItemStatistics(null, itemId, orderDate, orderCount, LocalDateTime.now());
    }

    public PopularItemStatistics(Long id, long itemId, LocalDate orderDate, int orderCount, LocalDateTime createdAt) {

        if (itemId < 0) {
            throw new IllegalArgumentException("상품식별자는 음수일 수 없습니다.");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("주문날짜 정보가 필요합니다.");
        }
        if (orderCount < 0) {
            throw new IllegalArgumentException("주문 수량은 음수일 수 없습니다.");
        }

        this.id = id;
        this.itemId = itemId;
        this.orderDate = orderDate;
        this.orderCount = orderCount;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopularItemStatistics that = (PopularItemStatistics) o;
        return itemId == that.itemId && orderCount == that.orderCount && Objects.equals(id, that.id) && Objects.equals(orderDate, that.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, orderDate, orderCount);
    }
}
