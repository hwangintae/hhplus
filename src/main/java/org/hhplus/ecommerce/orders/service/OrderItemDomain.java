package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderItemDomain {
    private final Long id;
    private final Long ordersId;
    private final Long itemId;
    private final int itemCnt;

    @Builder
    protected OrderItemDomain(Long id, Long ordersId, Long itemId, int itemCnt) {
        this.id = id;
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
    }
}
