package kr.hhplus.be.server.domain.order;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    private Long couponIssueId;

    private OrderStatus orderStatus;

    @Embedded
    private OrderAmountInfo orderAmountInfo;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Order of(User user) {
        return new Order(null, user, null, OrderStatus.COMPLETE, OrderAmountInfo.of(), LocalDateTime.now(), LocalDateTime.now());
    }

    public Order(Long id, User user, Long couponIssueId, OrderStatus orderStatus, OrderAmountInfo orderAmountInfo, LocalDateTime createdAt, LocalDateTime updatedAt) {

        if (user == null) {
            throw new IllegalArgumentException("유저 정보가 필요합니다.");
        }
        if (orderStatus == null) {
            throw new IllegalArgumentException("주문 상태 정보가 필요합니다.");
        }
        if (orderAmountInfo == null) {
            throw new IllegalArgumentException("주문 가격 정보가 필요합니다.");
        }

        this.id = id;
        this.user = user;
        this.couponIssueId = couponIssueId;
        this.orderStatus = orderStatus;
        this.orderAmountInfo = orderAmountInfo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void calculateOrderAmount(List<OrderItem> orderItems) {

        if (orderItems == null) {
            throw new IllegalArgumentException("주문 상품 정보가 필요합니다.");
        }

        int itemTotalAmount = orderItems.stream()
                .mapToInt(OrderItem::getOrderPrice)
                .sum();
        int discountAmount = 0;
        int totalAmount = itemTotalAmount - discountAmount;

        this.orderAmountInfo = OrderAmountInfo.of(totalAmount, itemTotalAmount, discountAmount);
    }

    public void applyDiscount(int discountAmount) {
        this.orderAmountInfo = orderAmountInfo.applyDiscount(discountAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(user, order.user) && Objects.equals(couponIssueId, order.couponIssueId) && orderStatus == order.orderStatus && Objects.equals(orderAmountInfo, order.orderAmountInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, couponIssueId, orderStatus, orderAmountInfo);
    }

    public long getUserId() {
        return user.getId();
    }

    public int getTotalAmount() {
        return orderAmountInfo.getTotalAmount();
    }
}
