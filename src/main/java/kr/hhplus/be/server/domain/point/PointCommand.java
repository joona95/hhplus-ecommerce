package kr.hhplus.be.server.domain.point;

public class PointCommand {

    public record PointChargeCommand(long userId, int amount) {
    }

    public record PointUseCommand(long userId, int amount) {
    }
}
