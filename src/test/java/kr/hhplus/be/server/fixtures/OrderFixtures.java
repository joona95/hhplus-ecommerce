package kr.hhplus.be.server.fixtures;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderAmountInfo;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderStatus;

import java.time.LocalDateTime;

public class OrderFixtures {

    public static Order 정상_주문_생성() {
        return new Order(null, 1L, 1L, OrderStatus.COMPLETE, OrderAmountInfo.of(30000, 50000, 20000), LocalDateTime.now(), LocalDateTime.now());
    }

    public static Order 유저식별자로_주문_생성(long userId) {
        return new Order(null, userId, 1L, OrderStatus.COMPLETE, OrderAmountInfo.of(30000, 50000, 20000), LocalDateTime.now(), LocalDateTime.now());
    }

    public static Order 주문상태로_주문_생성(OrderStatus orderStatus) {
        return new Order(null, 1L, 1L, orderStatus, OrderAmountInfo.of(30000, 50000, 20000), LocalDateTime.now(), LocalDateTime.now());
    }

    public static Order 주문가격정보로_주문_생성(OrderAmountInfo orderAmountInfo) {
        return new Order(null, 1L, 1L, OrderStatus.COMPLETE, orderAmountInfo, LocalDateTime.now(), LocalDateTime.now());
    }

    public static Order 생성일시로_주문_생성(LocalDateTime createdAt) {
        return new Order(null, 1L, 1L, OrderStatus.COMPLETE, OrderAmountInfo.of(30000, 50000, 20000), createdAt, createdAt);
    }

    public static OrderItem 주문식별자로_주문상품_생성(long orderId) {
        return new OrderItem(null, orderId, 1L, "상품명", 10000, 1);
    }

    public static OrderItem 상품식별자로_주문상품_생성(long itemId) {
        return new OrderItem(null, 1L, itemId, "상품명", 10000, 1);
    }

    public static OrderItem 상품명으로_주문상품_생성(String itemName) {
        return new OrderItem(null, 1L, 1L, itemName, 10000, 1);
    }

    public static OrderItem 상품가격으로_주문상품_생성(int sellPrice) {
        return new OrderItem(null, 1L, 1L, "상품명", sellPrice, 1);
    }

    public static OrderItem 주문수량으로_주문상품_생성(int count) {
        return new OrderItem(null, 1L, 1L, "상품명", 10000, count);
    }

    public static OrderItem 정상_주문상품_생성() {
        return new OrderItem(null, 1L, 1L, "상품명", 10000, 5);
    }
}
