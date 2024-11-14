package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.orders.usecase.OrderStatus;

import java.time.LocalDate;

@Getter
public class OrderItemRequest {

    private final Long orderItemId;
    private final Long ordersId;
    private final Long itemId;
    private final int itemCnt;
    private final LocalDate orderedAt;
    private final OrderStatus status;

    @Builder
    protected OrderItemRequest(Long orderItemId, Long ordersId, Long itemId, int itemCnt, LocalDate orderedAt, OrderStatus status) {
        this.orderItemId = orderItemId;
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.orderedAt = orderedAt;
        this.status = status;
    }
}
