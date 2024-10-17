package org.hhplus.ecommerce.cart.service;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CartItemCreate {

    private final Long itemId;
    private final int cnt;

    @Builder
    protected CartItemCreate(Long itemId, int cnt) {
        this.itemId = itemId;
        this.cnt = cnt;
    }
}
