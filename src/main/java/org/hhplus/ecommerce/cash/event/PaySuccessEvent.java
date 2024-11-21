package org.hhplus.ecommerce.cash.event;

import lombok.Getter;

import java.util.List;

@Getter
public class PaySuccessEvent {
    private final Long orderId;
    private final List<Long> orderItemIds;

    public PaySuccessEvent(Long orderId, List<Long> orderItemIds) {
        this.orderId = orderId;
        this.orderItemIds = orderItemIds;
    }
}
