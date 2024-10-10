package org.hhplus.ecommerce.item.service;

import lombok.Getter;

@Getter
public class StockDomain {

    private Long id;
    private Long itemId;
    private int quantity;
}
