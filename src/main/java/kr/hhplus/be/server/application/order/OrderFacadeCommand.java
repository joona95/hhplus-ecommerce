package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponCriteria;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.ItemCommand;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.point.PointCommand;
import kr.hhplus.be.server.domain.user.User;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderFacadeCommand {

    public record OrderCreateFacadeCommand(
            Long couponId,
            List<OrderItemCreateFacadeCommand> orderItemCommands
    ) {

        public static OrderCreateFacadeCommand of(Long couponId, List<OrderItemCreateFacadeCommand> itemCommands) {
            return new OrderCreateFacadeCommand(couponId, itemCommands);
        }

        public OrderCommand.OrderCreateCommand toOrderCreateCommand(User user, List<Item> items) {

            Map<Long, Item> itemMap = items.stream().collect(Collectors.toMap(Item::getId, Function.identity(), (o1, o2) -> o1));

            List<OrderCommand.OrderItemCreateCommand> orderItemCreateCommands = orderItemCommands.stream()
                    .map(orderItem -> orderItem.toOrderItemCreateCommand(itemMap.get(orderItem.itemId)))
                    .toList();

            return OrderCommand.OrderCreateCommand.of(user, orderItemCreateCommands);
        }

        public PointCommand.PointUseCommand toPointUseCommand(Order order) {
            return PointCommand.PointUseCommand.of(order);
        }

        public CouponCriteria.IssuedCouponCriteria toIssuedCouponCriteria(User user) {
            return CouponCriteria.IssuedCouponCriteria.of(user, couponId);
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
