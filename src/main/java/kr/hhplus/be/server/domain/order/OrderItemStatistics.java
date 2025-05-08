package kr.hhplus.be.server.domain.order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderItemStatistics {

    private final Map<Long, List<OrderItem>> itemsGroupedByItemId;

    public OrderItemStatistics(List<OrderItem> orderItems) {
        this.itemsGroupedByItemId = Optional.ofNullable(orderItems)
                .orElse(List.of()).stream()
                .collect(Collectors.groupingBy(OrderItem::getItemId));
    }

    public Set<Long> getItemIds() {
        return itemsGroupedByItemId.keySet();
    }

    public int getTotalOrderCount(long itemId) {
        return itemsGroupedByItemId.get(itemId).stream()
                .mapToInt(OrderItem::getCount)
                .sum();
    }

    public LocalDate getOrderDate(long itemId) {
        return itemsGroupedByItemId.get(itemId).get(0).getOrderDate();
    }
}
