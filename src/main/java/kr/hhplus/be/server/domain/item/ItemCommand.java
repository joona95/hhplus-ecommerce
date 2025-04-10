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
}
