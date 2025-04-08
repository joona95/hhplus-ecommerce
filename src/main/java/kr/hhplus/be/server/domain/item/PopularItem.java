package kr.hhplus.be.server.domain.item;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class PopularItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long itemId;

    private String itemName;

    private int price;

    private LocalDate orderDate;

    private int orderCount;

    @CreatedDate
    private LocalDateTime createdAt;

    public static PopularItem of(Long id, long itemId, String itemName, int price, LocalDate orderDate, int orderCount) {
        return new PopularItem(id, itemId, itemName, price, orderDate, orderCount, LocalDateTime.now());
    }

    public PopularItem(Long id, long itemId, String itemName, int price, LocalDate orderDate, int orderCount, LocalDateTime createdAt) {

        if (itemId < 0) {
            throw new IllegalArgumentException("상품식별자는 음수일 수 없습니다.");
        }
        if (!StringUtils.hasText(itemName)) {
            throw new IllegalArgumentException("상품명을 입력해주세요.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("상품 가격은 음수일 수 없습니다.");
        }
        if (orderDate == null) {
            throw new IllegalArgumentException("주문날짜 정보가 필요합니다.");
        }
        if (orderCount < 0) {
            throw new IllegalArgumentException("주문 수량은 음수일 수 없습니다.");
        }

        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.orderDate = orderDate;
        this.orderCount = orderCount;
        this.createdAt = createdAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopularItem that = (PopularItem) o;
        return itemId == that.itemId && price == that.price && orderCount == that.orderCount && Objects.equals(id, that.id) && Objects.equals(itemName, that.itemName) && Objects.equals(orderDate, that.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemId, itemName, price, orderDate, orderCount);
    }
}
