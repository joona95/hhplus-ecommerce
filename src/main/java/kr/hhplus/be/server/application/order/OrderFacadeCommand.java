package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemCommand;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.point.PointCommand;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderFacadeCommand {

    public record OrderCreateFacadeCommand(
            long userId,
            List<OrderItemCreateFacadeCommand> orderItems
    ) {

        public static OrderCreateFacadeCommand of(long userId, List<OrderItemCreateFacadeCommand> itemCommands) {
            return new OrderCreateFacadeCommand(userId, itemCommands);
        }

        public List<ItemCommand.StockDecreaseCommand> toStockDecreaseCommands() {
            return orderItems.stream()
                    .map(OrderItemCreateFacadeCommand::toStockDecreaseCommand)
                    .toList();
        }

        public OrderCommand.OrderCreateCommand toOrderCreateCommand(List<Item> items) {

            Map<Long, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity()));

            List<OrderCommand.OrderItemCreateCommand> orderItemCreateCommands = orderItems.stream()
                    .map(orderItem -> orderItem.toOrderItemCreateCommand(itemMap.get(orderItem.itemId)))
                    .toList();

            return OrderCommand.OrderCreateCommand.of(userId, orderItemCreateCommands);
        }

        public PointCommand.PointUseCommand toPointUseCommand(OrderInfo orderInfo) {
            return PointCommand.PointUseCommand.of(userId, orderInfo.order().getId(), orderInfo.getTotalAmount());
        }
    }

    public record OrderItemCreateFacadeCommand(
            long itemId,
            int count
    ) {

        public static OrderItemCreateFacadeCommand of(long itemId, int count) {
            return new OrderItemCreateFacadeCommand(itemId, count);
        }

        public ItemCommand.StockDecreaseCommand toStockDecreaseCommand() {
            return ItemCommand.StockDecreaseCommand.of(itemId, count);
        }

        public OrderCommand.OrderItemCreateCommand toOrderItemCreateCommand(Item item) {
            return OrderCommand.OrderItemCreateCommand.of(item, count);
        }
    }
}
