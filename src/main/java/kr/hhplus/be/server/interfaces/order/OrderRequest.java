package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

import static kr.hhplus.be.server.application.order.OrderFacadeCommand.*;

public class OrderRequest {

    @Schema(title = "주문 결제 요청값")
    public record OrderCreateRequest(
            @Schema(description = "유저식별자", example = "1")
            @Positive
            long userId,
            @Schema(description = "쿠폰식별자", example = "1")
            Long couponId,
            @Schema(description = "주문 상품 정보 목록", example = "[1, 2, 3]")
            @NotNull
            List<OrderItemCreateRequest> items
    ) {
        public OrderCreateFacadeCommand toCommand() {

            List<OrderItemCreateFacadeCommand> itemCommands = items.stream()
                    .map(OrderItemCreateRequest::toCommand)
                    .toList();

            return OrderCreateFacadeCommand.of(userId, couponId, itemCommands);
        }
    }

    @Schema(title = "주문 상품 요청값")
    public record OrderItemCreateRequest(
            @Schema(description = "상품식별자", example = "1")
            @Positive
            long itemId,
            @Schema(description = "주문수량", example = "1")
            @Positive
            int count
    ) {
        public OrderItemCreateFacadeCommand toCommand() {
            return OrderItemCreateFacadeCommand.of(itemId, count);
        }
    }
}
