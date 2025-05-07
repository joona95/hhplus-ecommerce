package kr.hhplus.be.server.interfaces.item;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.item.Item;
import kr.hhplus.be.server.domain.item.PopularItemDetail;

public class ItemResponse {

    @Schema(title = "상품 상세 정보 응답값")
    public record ItemDetailResponse(@Schema(description = "상품식별자", example = "1") long id,
                                     @Schema(description = "상품명", example = "상품명") String itemName,
                                     @Schema(description = "상품 가격", example = "1000") long price,
                                     @Schema(description = "상품 재고", example = "100") long stock) {

        public static ItemDetailResponse from(Item item) {
            return new ItemDetailResponse(item.getId(), item.getItemName(), item.getPrice(), item.getStock());
        }
    }

    @Schema(title = "인기 상품 상세 정보 응답값")
    public record PopularItemDetailResponse(@Schema(description = "상품식별자", example = "1") long itemId,
                                            @Schema(description = "상품명", example = "상품명") String itemName,
                                            @Schema(description = "상품 가격", example = "1000") long price,
                                            @Schema(description = "총 주문 수량", example = "100") long orderCount) {

        public static PopularItemDetailResponse from(PopularItemDetail popularItemDetail) {
            return new PopularItemDetailResponse(popularItemDetail.itemId(), popularItemDetail.itemName(), popularItemDetail.price(), popularItemDetail.orderCount());
        }
    }
}
