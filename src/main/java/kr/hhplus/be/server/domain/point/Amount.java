package kr.hhplus.be.server.domain.point;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Objects;

@Embeddable
@Getter
public class Amount {

    private final int value;

    public static Amount of(int value) {
        return new Amount(value);
    }

    protected Amount() {
        this.value = 0;
    }

    public Amount(int value) {

        if (value < 0) {
            throw new IllegalArgumentException("금액은 음수일 수 없습니다.");
        }

        this.value = value;
    }

    public Amount plus(int value) {
        return Amount.of(this.value + value);
    }

    public Amount minus(int value) {
        return Amount.of(this.value - value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amount amount = (Amount) o;
        return value == amount.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
