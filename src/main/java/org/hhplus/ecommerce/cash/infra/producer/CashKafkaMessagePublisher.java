package org.hhplus.ecommerce.cash.infra.producer;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.event.PayFailEvent;
import org.hhplus.ecommerce.cash.event.PaySuccessEvent;
import org.hhplus.ecommerce.item.event.SubStockSuccessEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashKafkaMessagePublisher {

    private final KafkaTemplate<String, PaySuccessEvent> kafkaTemplate;

    public void publish(PaySuccessEvent event) {

        kafkaTemplate.send(MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "cash-pay-topic")
                .build());
    }

    public void publish(PayFailEvent event) {

        kafkaTemplate.send(MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, "cash-pay-fail-topic")
                .build());
    }
}
