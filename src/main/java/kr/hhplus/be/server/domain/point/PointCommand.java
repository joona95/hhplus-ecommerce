package kr.hhplus.be.server.domain.point;

public class PointCommand {

    public record PointChargeCommand(int amount) {
    }

    public record PointUseCommand(long orderId, int amount) {
        public static PointUseCommand of(long orderId, int amount) {
            return new PointUseCommand(orderId, amount);
        }
    }
}
