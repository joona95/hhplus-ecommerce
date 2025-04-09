package kr.hhplus.be.server.interfaces.point;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.point.Point;

import java.time.LocalDateTime;

public class PointResponse {

    @Schema(title = "유저 잔액 정보 응답값")
    public record UserPointResponse(@Schema(description = "유저식별자", example = "1") long userId,
                                    @Schema(description = "잔액", example = "1000") int amount,
                                    @Schema(description = "수정일시", example = "2025-01-01T00:00:00.000000") LocalDateTime updatedAt
    ) {

        public static UserPointResponse from(Point point) {
            return new UserPointResponse(point.getUserId(), point.getAmount(), point.getUpdatedAt());
        }
    }
}
