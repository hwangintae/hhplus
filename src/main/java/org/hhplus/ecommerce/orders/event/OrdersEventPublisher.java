package org.hhplus.ecommerce.orders.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdersEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public void success(OrderingSuccessEvent event) {

        eventPublisher.publishEvent(event);
    }
}
