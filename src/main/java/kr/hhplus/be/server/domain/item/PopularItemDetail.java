package kr.hhplus.be.server.domain.item;

public record PopularItemDetail(
        long itemId,
        String itemName,
        int price,
        int orderCount
) {

    public static PopularItemDetail of(PopularItem popularItem, Item item) {
        return new PopularItemDetail(popularItem.getItemId(), item.getItemName(), item.getPrice(), popularItem.getOrderCount());
    }
}
