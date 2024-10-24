package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItem;

@Getter
public class OrderItemDomain {
    private final Long id;
    private final Long ordersId;
    private final Long itemId;
    private final int itemCnt;
    private final boolean deleteAt;

    @Builder
    protected OrderItemDomain(Long id, Long ordersId, Long itemId, int itemCnt, boolean deleteAt) {
        this.id = id;
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.deleteAt = deleteAt;
    }

    public OrderItem toEntity() {
        return OrderItem.builder()
                .id(this.id)
                .ordersId(this.ordersId)
                .itemId(this.itemId)
                .itemCnt(this.itemCnt)
                .build();
    }
}
