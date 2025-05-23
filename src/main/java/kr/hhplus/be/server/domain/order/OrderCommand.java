package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.user.User;

import java.util.List;

public class OrderCommand {

    public record OrderCreateCommand(
            User user,
            List<OrderItemCreateCommand> orderItemCreateCommands
    ) {

        public static OrderCreateCommand of(User user, List<OrderItemCreateCommand> orderItemCreateCommands) {
            return new OrderCreateCommand(user, orderItemCreateCommands);
        }

        public Order toOrder() {
            return Order.of(user);
        }

        public OrderItems toOrderItems(Order order) {
            return new OrderItems(orderItemCreateCommands.stream()
                    .map(orderItemCreateCommand -> orderItemCreateCommand.toOrderItem(order))
                    .toList());
        }
    }

    public record OrderItemCreateCommand(
            Item item,
            int count
    ) {

        public static OrderItemCreateCommand of(Item item, int count) {
            return new OrderItemCreateCommand(item, count);
        }

        public OrderItem toOrderItem(Order order) {
            return OrderItem.of(order, item, count);
        }
    }
}
