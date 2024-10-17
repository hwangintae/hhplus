package org.hhplus.ecommerce.item.usecase;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.item.service.ItemDomain;
import org.hhplus.ecommerce.item.service.StockDomain;

@Getter
public class ItemResponse {
    private Long id;
    private final String name;
    private final long price;
    private final int quantity;

    @Builder
    protected ItemResponse(Long id, String name, long price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public static ItemResponse of(ItemDomain domain, StockDomain stockDomain) {
        return ItemResponse.builder()
                .id(domain.getId())
                .name(domain.getName())
                .price(domain.getPrice())
                .quantity(stockDomain.getQuantity())
                .build();

    }
}
