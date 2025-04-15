package kr.hhplus.be.server.fixtures;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.Stock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ItemFixtures {

    public static Item 상품명으로_상품_생성(String itemName) {
        return new Item(1L, itemName, Stock.of(1000), 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 재고로_상품_생성(Stock stock) {
        return new Item(1L, "상품명", stock, 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 가격으로_상품_생성(int price) {
        return new Item(1L, "상품명", Stock.of(1000), price, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 정상_상품_생성() {
        return Item.of(1L, "상품명", Stock.of(100), 10000);
    }

    public static List<Item> 정상_상품_목록_생성() {
        return List.of(
                Item.of(1L, "상품명1", Stock.of(10), 10000),
                Item.of(2L, "상품명2", Stock.of(10), 20000)
        );
    }

    public static PopularItem 상품식별자로_인기_상품_생성(long itemId) {
        return new PopularItem(1L, itemId, "상품명", 1000, LocalDate.now(), 100, LocalDateTime.now());
    }

    public static PopularItem 주문날짜로_인기_상품_생성(LocalDate orderDate) {
        return new PopularItem(1L, 1L, "상품명", 1000, orderDate, 100, LocalDateTime.now());
    }

    public static PopularItem 주문수량으로_인기_상품_생성(int orderCount) {
        return new PopularItem(1L, 1L, "상품명", 1000, LocalDate.now(), orderCount, LocalDateTime.now());
    }
}
