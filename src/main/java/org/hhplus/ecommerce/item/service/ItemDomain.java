package org.hhplus.ecommerce.item.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.item.infra.jpa.Item;

@Getter
public class ItemDomain {
    private final Long id;
    private final String name;
    private final long price;

    @Builder
    protected ItemDomain(Long id, String name, long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Item toEntity() {
        return Item.builder()
                .id(this.id)
                .name(this.name)
                .price(this.price)
                .build();
    }
}
