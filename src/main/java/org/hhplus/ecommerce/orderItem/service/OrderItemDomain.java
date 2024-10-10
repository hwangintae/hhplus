package org.hhplus.ecommerce.orderItem.service;

import lombok.Getter;

@Getter
public class OrderItemDomain {
    private Long id;
    private Long orderId;
    private Long itemId;
    private int itemCnt;
}
