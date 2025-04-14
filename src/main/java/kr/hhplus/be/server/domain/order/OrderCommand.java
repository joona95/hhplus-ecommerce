package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.item.Item;

import java.util.List;

public class OrderCommand {

    public record OrderCreateCommand(
            long userId,
            List<OrderItemCreateCommand> orderItemCreateCommands
    ) {

        public static OrderCreateCommand of(long userId, List<OrderItemCreateCommand> orderItemCreateCommands) {
            return new OrderCreateCommand(userId, orderItemCreateCommands);
        }
    }

    public record OrderItemCreateCommand(
            Item item,
            int count
    ) {

        public static OrderItemCreateCommand of(Item item, int count) {
            return new OrderItemCreateCommand(item, count);
        }
    }
}
