package org.hhplus.ecommerce.item.event;

import lombok.Getter;
import org.hhplus.ecommerce.item.service.ItemInfo;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;

import java.util.List;

@Getter
public class SubStockFailEvent {
    private final Long userId;
    private final List<OrderItemInfo> orderItemInfos;

    public SubStockFailEvent(Long userId, List<OrderItemInfo> orderItemInfos) {
        this.userId = userId;
        this.orderItemInfos = orderItemInfos;
    }
}
