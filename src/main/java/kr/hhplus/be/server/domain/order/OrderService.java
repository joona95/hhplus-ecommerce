package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.hhplus.be.server.domain.order.OrderCommand.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderInfo createOrder(OrderCreateCommand command) {

        Order order = orderRepository.saveOrder(command.toOrder());

        OrderItems orderItems = command.toOrderItems(order);

        order.calculateOrderAmount(orderItems);

        orderRepository.saveOrderItems(orderItems.orderItems());

        return OrderInfo.of(order, orderItems);
    }

    public OrderItemStatistics findYesterdayOrderItemStatistics() {
        return new OrderItemStatistics(orderRepository.findYesterdayOrderItems());
    }
}
