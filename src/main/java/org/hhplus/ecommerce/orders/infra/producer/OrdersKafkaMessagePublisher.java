package org.hhplus.ecommerce.orders.infra.producer;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.event.OrderPaySuccessEvent;
import org.hhplus.ecommerce.orders.event.OrderSuccessEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdersKafkaMessagePublisher {

    private final KafkaTemplate<String, OrderSuccessEvent> kafkaTemplate;

    public void publish(OrderSuccessEvent event) {

        kafkaTemplate.send(MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-init-topic")
                .build());
    }

    public void publish(OrderPaySuccessEvent event) {

        kafkaTemplate.send(MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "order-pay-success-topic")
                .build());
    }
}
