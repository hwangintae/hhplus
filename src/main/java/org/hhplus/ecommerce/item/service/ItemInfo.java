package org.hhplus.ecommerce.item.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.item.infra.jpa.Item;

@Getter
public class ItemInfo {
    private final Long itemId;
    private final long price;

    @Builder
    protected ItemInfo(Long itemId, long price) {
        this.itemId = itemId;
        this.price = price;
    }
}
