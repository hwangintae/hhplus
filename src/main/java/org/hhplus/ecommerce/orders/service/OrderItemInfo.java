package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OrderItemInfo {

    private final Long orderItemId;
    private final Long itemId;
    private final int itemCnt;
    private final LocalDate orderedAt;
    private final OrderStatus status;

    @Builder
    protected OrderItemInfo(Long orderItemId, Long itemId, int itemCnt, LocalDate orderedAt, OrderStatus status) {
        this.orderItemId = orderItemId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.orderedAt = orderedAt;
        this.status = status;
    }
}
