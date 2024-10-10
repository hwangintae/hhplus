package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {
    private final String itemName;
    private final int itemCnt;
    private final OrderStatus status;

    @Builder
    protected OrderResponse(String itemName, int itemCnt, OrderStatus status) {
        this.itemName = itemName;
        this.itemCnt = itemCnt;
        this.status = status;
    }
}
