package kr.hhplus.be.server.domain.item;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    @Embedded
    private Stock stock;

    private int price;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Item of(Long id, String itemName, Stock stock, int price) {
        return new Item(id, itemName, stock, price, LocalDateTime.now(), LocalDateTime.now());
    }

    public Item(Long id, String itemName, Stock stock, int price, LocalDateTime createdAt, LocalDateTime updatedAt) {


        if (!StringUtils.hasText(itemName)) {
            throw new IllegalArgumentException("상품명을 입력해주세요.");
        }
        if (stock == null) {
            throw new IllegalArgumentException("재고 정보가 필요합니다.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("가격은 음수가 될 수 없습니다.");
        }

        this.id = id;
        this.itemName = itemName;
        this.stock = stock;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void decreaseStock(int count) {
        this.stock = stock.decrease(count);
    }

    public void increaseStock(int count) {
        this.stock = stock.increase(count);
    }

    public int getStock() {
        return stock.getCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return price == item.price && Objects.equals(id, item.id) && Objects.equals(itemName, item.itemName) && Objects.equals(stock, item.stock);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, itemName, stock, price);
    }
}
