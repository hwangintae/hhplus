package org.hhplus.ecommerce.orders.controller;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.orders.usecase.OrderStatus;

@Getter
public class OrderResponse {
    private final Long ordersId;
    private final Long itemId;
    private final int itemCnt;

    @Builder
    protected OrderResponse(Long ordersId, Long itemId, int itemCnt) {
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
    }
}
