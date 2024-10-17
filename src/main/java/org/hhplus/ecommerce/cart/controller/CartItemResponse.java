package org.hhplus.ecommerce.cart.controller;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.cart.service.CartItemStatus;

@Getter
public class CartItemResponse {
    private final Long itemId;
    private final int cnt;
    private final boolean deleteAt;

    @Builder
    protected CartItemResponse(Long itemId, int cnt, boolean deleteAt) {
        this.itemId = itemId;
        this.cnt = cnt;
        this.deleteAt = deleteAt;
    }
}
