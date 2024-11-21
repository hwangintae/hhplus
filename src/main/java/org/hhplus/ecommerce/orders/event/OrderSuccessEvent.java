package org.hhplus.ecommerce.orders.event;

import lombok.Getter;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;

import java.util.List;

@Getter
public class OrderSuccessEvent {

    private final Long userId;
    private final Long orderId;
    private final List<OrderItemInfo> orderItemInfos;

    public OrderSuccessEvent(Long userId, Long orderId, List<OrderItemInfo> orderItemInfos) {
        this.userId = userId;
        this.orderId = orderId;
        this.orderItemInfos = orderItemInfos;
    }
}
