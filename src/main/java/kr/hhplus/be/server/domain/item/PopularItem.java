package kr.hhplus.be.server.domain.item;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class PopularItem implements Serializable {

    private long itemId;

    private int orderCount;

    public PopularItem(long itemId, int orderCount) {

        this.itemId = itemId;
        this.orderCount = orderCount;
    }
}
