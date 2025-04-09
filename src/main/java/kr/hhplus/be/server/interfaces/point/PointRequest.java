package kr.hhplus.be.server.interfaces.point;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.domain.point.PointCommand;

public class PointRequest {
    @Schema(title = "유저 잔액 충전 요청값")
    public record PointChargeRequest(@Schema(description = "유저식별자", example = "1") @Positive long userId,
                                     @Schema(description = "충전 금액", example = "1000") @Positive int amount
    ) {
        public PointCommand.PointChargeCommand toCommand() {
            return new PointCommand.PointChargeCommand(userId, amount);
        }
    }
}

