package kr.hhplus.be.server.domain.point;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
public class Point {

    private static final int MAX_POINT_LIMIT = 1000000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long userId;

    @Embedded
    private Amount amount;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Point of(Long id, long userId, Amount amount) {
        return new Point(id, userId, amount, LocalDateTime.now());
    }

    public Point(Long id, long userId, Amount amount, LocalDateTime updatedAt) {

        if (userId < 0) {
            throw new IllegalArgumentException("유저식별자는 음수일 수 없습니다.");
        }
        if (amount == null) {
            throw new IllegalArgumentException("잔액 정보가 필요합니다.");
        }

        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.updatedAt = updatedAt;
    }

    public Point charge(int value) {

        if (amount.getValue() + value > MAX_POINT_LIMIT) {
            throw new IllegalArgumentException("최대 한도를 초과하여 충전할 수 없습니다.");
        }

        return Point.of(this.id, this.userId, this.amount.plus(value));
    }

    public Point use(int value) {

        if (amount.getValue() - value < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        return Point.of(this.id, this.userId, this.amount.minus(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return userId == point.userId && amount == point.amount && Objects.equals(id, point.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, amount);
    }
}
