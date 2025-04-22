package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.order.Order;

public class PointCommand {

    public record PointChargeCommand(int amount) {
    }

    public record PointUseCommand(Order order) {
        public static PointUseCommand of(Order order) {
            return new PointUseCommand(order);
        }
    }
}
