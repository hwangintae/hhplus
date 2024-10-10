package org.hhplus.ecommerce.cart.service;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CartResponse {
    private final String itemName;
    private final int itemCnt;
    private final CartItemStatus status;

    @Builder
    protected CartResponse(String itemName, int itemCnt, CartItemStatus status) {
        this.itemName = itemName;
        this.itemCnt = itemCnt;
        this.status = status;
    }
}
