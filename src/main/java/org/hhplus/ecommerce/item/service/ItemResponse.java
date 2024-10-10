package org.hhplus.ecommerce.item.service;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ItemResponse {
    private final String name;
    private final BigDecimal price;
    private final int quantity;

    @Builder
    protected ItemResponse(String name, BigDecimal price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}
