package kr.hhplus.be.server.interfaces.item;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.item.ItemCommand;

public class ItemRequest {

    @Schema(title = "상품 수정 요청값")
    public record ItemUpdateRequest(
            @Schema(description = "상품명", example = "상품명")
            String itemName,
            @Schema(description = "가격", example = "1000")
            int price,
            @Schema(description = "재고", example = "100")
            int stock
            ) {

        public ItemCommand.ItemUpdateCommand toCommand() {
            return ItemCommand.ItemUpdateCommand.of(itemName, price, stock);
        }
    }
}