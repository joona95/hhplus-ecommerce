package kr.hhplus.be.server.domain.point;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import kr.hhplus.be.server.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Point {

    private static final int MAX_POINT_LIMIT = 1000000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Embedded
    private Amount amount;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public static Point of(User user, Amount amount) {
        return new Point(null, user, amount, LocalDateTime.now());
    }

    public Point(Long id, User user, Amount amount, LocalDateTime updatedAt) {

        if (user == null) {
            throw new IllegalArgumentException("유저 정보가 필요합니다.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("잔액 정보가 필요합니다.");
        }

        this.id = id;
        this.user = user;
        this.amount = amount;
        this.updatedAt = updatedAt;
    }

    public void charge(int value) {

        if (amount.getValue() + value > MAX_POINT_LIMIT) {
            throw new IllegalArgumentException("최대 한도를 초과하여 충전할 수 없습니다.");
        }

        this.amount = this.amount.plus(value);
    }

    public void use(int value) {

        if (amount.getValue() - value < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        this.amount = this.amount.minus(value);
    }

    public int getAmount() {
        return amount.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Objects.equals(id, point.id) && Objects.equals(user, point.user) && Objects.equals(amount, point.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, amount);
    }
}
