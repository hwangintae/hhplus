package org.hhplus.ecommerce.item.event;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SubStockOutboxJsonData {
    private final Long itemId;
    private final int itemCnt;
    private final long price;

    @Builder
    protected SubStockOutboxJsonData(Long itemId, int itemCnt, long price) {
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.price = price;
    }
}
