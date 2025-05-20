package kr.hhplus.be.server.domain.item;

public class ItemEvent {

    public record StockDecreasedEvent(
            Long itemId,
            int count
    ) {
    }
}
