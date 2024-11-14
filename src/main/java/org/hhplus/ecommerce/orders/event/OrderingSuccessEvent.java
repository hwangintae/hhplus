package org.hhplus.ecommerce.orders.event;

import lombok.Getter;
import org.hhplus.ecommerce.orders.service.OrderItemRequest;

import java.util.List;

@Getter
public class OrderingSuccessEvent {

    private final List<OrderItemRequest> orderItemRequests;

    public OrderingSuccessEvent(List<OrderItemRequest> orderItemRequests) {
        this.orderItemRequests = orderItemRequests;
    }
}
