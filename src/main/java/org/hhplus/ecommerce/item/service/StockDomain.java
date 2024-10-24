package org.hhplus.ecommerce.item.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.hhplus.ecommerce.item.infra.jpa.Stock;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.INSUFFICIENT_STOCK;

@Getter
public class StockDomain {

    private final Long id;
    private final Long itemId;
    private int quantity;

    @Builder
    protected StockDomain(Long id, Long itemId, int quantity) {
        this.id = id;
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public void addQuantity(int cnt) {
        this.quantity += cnt;
    }

    public void subQuantity(int cnt) {
        if (quantity - cnt < 0) {
            throw new IllegalArgumentException(INSUFFICIENT_STOCK.getMessage());
        }

        this.quantity -= cnt;
    }

    public boolean checkQuantity(int cnt) {
        return quantity - cnt >= 0;
    }

    public Stock toEntity() {
        return Stock.builder()
                .id(this.id)
                .itemId(this.itemId)
                .quantity(this.quantity)
                .build();
    }
}
