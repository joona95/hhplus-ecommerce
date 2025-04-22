package kr.hhplus.be.server.infrastructure.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderItemJpaRepository orderItemJpaRepository;
    private final OrderQuerydslRepository orderQuerydslRepository;

    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository, OrderItemJpaRepository orderItemJpaRepository, OrderQuerydslRepository orderQuerydslRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderItemJpaRepository = orderItemJpaRepository;
        this.orderQuerydslRepository = orderQuerydslRepository;
    }

    @Override
    public Order saveOrder(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public List<OrderItem> saveOrderItems(List<OrderItem> orderItems) {
        return orderItemJpaRepository.saveAll(orderItems);
    }

    @Override
    public List<OrderItem> findTodayOrderItems() {
        return orderQuerydslRepository.findTodayOrderItems();
    }
}
