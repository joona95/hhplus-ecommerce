package kr.hhplus.be.server.fixtures;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.PopularItem;
import kr.hhplus.be.server.domain.item.PopularItemStatistics;
import kr.hhplus.be.server.domain.item.Stock;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ItemFixtures {

    public static Item 식별자로_상품_생성(long id) {
        return new Item(id, "상품명", Stock.of(1000), 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 상품명으로_상품_생성(String itemName) {
        return new Item(null, itemName, Stock.of(1000), 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 재고로_상품_생성(Stock stock) {
        return new Item(null, "상품명", stock, 100, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 가격으로_상품_생성(int price) {
        return new Item(null, "상품명", Stock.of(1000), price, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 식별자와_가격으로_상품_생성(long id, int price) {
        return new Item(id, "상품명", Stock.of(1000), price, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Item 정상_상품_생성() {
        return Item.of(null, "상품명", Stock.of(100), 10000);
    }

    public static PopularItem 상품식별자로_인기_상품_생성(long itemId) {
        return new PopularItem(itemId, 100);
    }

    public static PopularItemStatistics 상품식별자로_인기_상품_통계_생성(long itemId) {
        return new PopularItemStatistics(null, itemId, LocalDate.now(), 100, LocalDateTime.now());
    }

    public static PopularItemStatistics 주문날짜로_인기_상품_통계_생성(LocalDate orderDate) {
        return new PopularItemStatistics(null, 1L, orderDate, 100, LocalDateTime.now());
    }

    public static PopularItemStatistics 주문수량으로_인기_상품_통계_생성(int orderCount) {
        return new PopularItemStatistics(null, 1L, LocalDate.now(), orderCount, LocalDateTime.now());
    }

    public static PopularItemStatistics 상품식별자와_주문날짜와_주문수량으로_인기_상품_통계_생성(long itemId, LocalDate orderDate, int orderCount) {
        return new PopularItemStatistics(null, itemId, orderDate, orderCount, LocalDateTime.now());
    }
}
