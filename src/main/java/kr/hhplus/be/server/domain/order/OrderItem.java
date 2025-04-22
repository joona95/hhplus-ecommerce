package kr.hhplus.be.server.domain.order;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    private long itemId;

    private String itemName;

    private int sellPrice;

    private int count;

    public static OrderItem of(Order order, Item item, int count) {

        if (item == null) {
            throw new IllegalArgumentException("상품 정보가 필요합니다.");
        }

        return new OrderItem(null, order, item.getId(), item.getItemName(), item.getPrice(), count);
    }

    public OrderItem(Long id, Order order, long itemId, String itemName, int sellPrice, int count) {

        if (order == null) {
            throw new IllegalArgumentException("주문 정보가 필요합니다.");
        }
        if (itemId < 0) {
            throw new IllegalArgumentException("상품식별자는 음수일 수 없습니다.");
        }
        if (!StringUtils.hasText(itemName)) {
            throw new IllegalArgumentException("상품명을 입력해주세요.");
        }
        if (sellPrice < 0) {
            throw new IllegalArgumentException("상품 가격은 음수일 수 없습니다.");
        }
        if (count <= 0) {
            throw new IllegalArgumentException("주문 수량은 양수여야 합니다.");
        }

        this.id = id;
        this.order = order;
        this.itemId = itemId;
        this.itemName = itemName;
        this.sellPrice = sellPrice;
        this.count = count;
    }

    public int getOrderItemPrice() {
        return sellPrice * count;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return itemId == orderItem.itemId && sellPrice == orderItem.sellPrice && count == orderItem.count && Objects.equals(id, orderItem.id) && Objects.equals(order, orderItem.order) && Objects.equals(itemName, orderItem.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, itemId, itemName, sellPrice, count);
    }
}
