package kr.hhplus.be.server.domain.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderInfo createOrder(OrderCommand.OrderCreateCommand command) {

        Order order = orderRepository.saveOrder(Order.of(command.userId()));

        List<OrderItem> orderItems = command.orderItemCreateCommands().stream()
                .map(orderItemCreateCommand -> OrderItem.of(
                        order,
                        orderItemCreateCommand.item(),
                        orderItemCreateCommand.count()))
                .toList();

        order.calculateOrderAmount(orderItems);

        orderItems = orderRepository.saveOrderItems(orderItems);

        return OrderInfo.of(order, orderItems);
    }
}
