package org.hhplus.ecommerce.item.event;

import lombok.Getter;
import org.hhplus.ecommerce.item.service.ItemInfo;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;

import java.util.List;

@Getter
public class SubStockSuccessEvent {
    private final Long userId;
    private final Long orderId;
    private final List<OrderItemInfo> orderItemInfos;
    private final List<ItemInfo> itemInfos;

    public SubStockSuccessEvent(Long userId, Long orderId, List<OrderItemInfo> orderItemInfos, List<ItemInfo> itemInfos) {
        this.userId = userId;
        this.orderId = orderId;
        this.orderItemInfos = orderItemInfos;
        this.itemInfos = itemInfos;
    }
}
