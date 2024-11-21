package org.hhplus.ecommerce.cash.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;

import java.util.List;

@Getter
public class PayFailEvent {
    private final Long orderId;
    private final List<OrderItemInfo> orderItemInfos;

    public PayFailEvent(Long orderId, List<OrderItemInfo> orderItemInfos) {
        this.orderId = orderId;
        this.orderItemInfos = orderItemInfos;
    }
}
