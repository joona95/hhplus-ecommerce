package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository {

    Order saveOrder(Order order);

    List<OrderItem> saveOrderItems(List<OrderItem> orderItems);
}
