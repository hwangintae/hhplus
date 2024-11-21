package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItem;

import java.time.LocalDate;

@Getter
public class OrderItemDomain {
    private final Long id;
    private final Long ordersId;
    private final Long itemId;
    private final int itemCnt;
    private final boolean deleteAt;
    private final LocalDate orderedAt;
    private OrderStatus status;

    @Builder
    protected OrderItemDomain(Long id, Long ordersId, Long itemId, int itemCnt, boolean deleteAt, LocalDate orderedAt, OrderStatus status) {
        this.id = id;
        this.ordersId = ordersId;
        this.itemId = itemId;
        this.itemCnt = itemCnt;
        this.deleteAt = deleteAt;
        this.orderedAt = orderedAt;
        this.status = status;
    }

    public static OrderItemDomain generateOrderItemDomain(Long ordersId, Long itemId, int itemCnt) {
        return OrderItemDomain.builder()
                .ordersId(ordersId)
                .itemId(itemId)
                .itemCnt(itemCnt)
                .deleteAt(false)
                .orderedAt(LocalDate.now())
                .status(OrderStatus.INIT)
                .build();
    }

    public OrderItem toEntity() {
        return OrderItem.builder()
                .id(this.id)
                .ordersId(this.ordersId)
                .itemId(this.itemId)
                .itemCnt(this.itemCnt)
                .deleteAt(this.deleteAt)
                .orderedAt(this.orderedAt)
                .status(this.status)
                .build();
    }

    public OrderItemInfo toInfo() {
        return OrderItemInfo.builder()
                .orderItemId(this.id)
                .itemId(this.itemId)
                .itemCnt(this.itemCnt)
                .orderedAt(this.orderedAt)
                .status(this.status)
                .build();
    }

    public void orderSuccess() {
        this.status = OrderStatus.SUCCESS;
    }

    public void orderFail() {
        this.status = OrderStatus.FAIL;
    }
}
