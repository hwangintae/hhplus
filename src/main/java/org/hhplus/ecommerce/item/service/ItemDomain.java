package org.hhplus.ecommerce.item.service;

import lombok.Builder;
import lombok.Getter;

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
}
