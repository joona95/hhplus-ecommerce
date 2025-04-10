package kr.hhplus.be.server.interfaces.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.order.OrderFacadeCommand;

import java.util.List;

public class OrderRequest {

    @Schema(title = "주문 결제 요청값")
    public record OrderCreateRequest(
            @Schema(description = "유저식별자", example = "1")
            @Positive
            long userId,
            @Schema(description = "주문 상품 정보 목록", example = "[1, 2, 3]")
            @NotNull
            List<OrderItemCreateRequest> items
    ) {
        public OrderFacadeCommand.OrderCreateFacadeCommand toCommand() {

            List<OrderFacadeCommand.OrderItemCreateFacadeCommand> itemCommands = items.stream()
                    .map(OrderItemCreateRequest::toCommand)
                    .toList();

            return OrderFacadeCommand.OrderCreateFacadeCommand.of(userId, itemCommands);
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
        public OrderFacadeCommand.OrderItemCreateFacadeCommand toCommand() {
            return OrderFacadeCommand.OrderItemCreateFacadeCommand.of(itemId, count);
        }
    }
}
