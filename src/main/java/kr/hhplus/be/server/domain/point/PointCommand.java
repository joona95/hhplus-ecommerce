package kr.hhplus.be.server.domain.point;

public class PointCommand {

    public record PointChargeCommand(long userId, int amount) {
    }

    public record PointUseCommand(long userId, long orderId, int amount) {
        public static PointUseCommand of(long userId, long orderId, int amount) {
            return new PointUseCommand(userId, orderId, amount);
        }
    }
}
