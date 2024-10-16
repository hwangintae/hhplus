package org.hhplus.ecommerce.orders.usecase;

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

    public static OrderResponse success(String itemName, int itemCnt) {
        return OrderResponse.builder()
                .itemName(itemName)
                .itemCnt(itemCnt)
                .status(OrderStatus.SUCCESS)
                .build();
    }

    public static OrderResponse fail(String itemName, int itemCnt) {
        return OrderResponse.builder()
                .itemName(itemName)
                .itemCnt(itemCnt)
                .status(OrderStatus.FAIL)
                .build();
    }
}
