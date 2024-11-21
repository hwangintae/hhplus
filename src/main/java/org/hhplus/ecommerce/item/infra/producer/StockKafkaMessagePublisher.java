package org.hhplus.ecommerce.item.infra.producer;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.item.event.SubStockFailEvent;
import org.hhplus.ecommerce.item.event.SubStockSuccessEvent;
import org.hhplus.ecommerce.orders.event.OrderSuccessEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockKafkaMessagePublisher {

    private final KafkaTemplate<String, SubStockSuccessEvent> kafkaTemplate;

    public void publish(SubStockSuccessEvent event) {

        kafkaTemplate.send(MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "sub-stock-topic")
                .build());
    }

    public void publish(SubStockFailEvent event) {

        kafkaTemplate.send(MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "sub-stock-fail-topic")
                .build());
    }
}
