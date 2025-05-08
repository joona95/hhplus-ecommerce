package kr.hhplus.be.server.domain.item;

public class ItemCommand {

    public record StockDecreaseCommand(
            long itemId,
            int count
    ) {
        public static StockDecreaseCommand of(long itemId, int count) {
            return new StockDecreaseCommand(itemId, count);
        }
    }

    public record ItemUpdateCommand(
            String itemName,
            int price,
            int stock
            ) {

        public static ItemUpdateCommand of(String itemName, int price, int stock) {
            return new ItemUpdateCommand(itemName, price, stock);
        }
    }
}
