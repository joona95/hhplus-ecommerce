package kr.hhplus.be.server.domain.item;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
public class Stock {

    private int count;

    public static Stock of(int count) {
        return new Stock(count);
    }

    public Stock(int count) {

        if (count < 0) {
            throw new IllegalArgumentException("재고는 음수가 될 수 없습니다.");
        }

        this.count = count;
    }

    public Stock decrease(int count) {

        if (this.count - count < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        return Stock.of(this.count - count);
    }

    public Stock increase(int count) {
        return Stock.of(this.count + count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return count == stock.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count);
    }
}
