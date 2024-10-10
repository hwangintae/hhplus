package org.hhplus.ecommerce.item.service;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public abstract class ItemDomain {
    private Long id;
    private String name;
    private BigDecimal price;
}
