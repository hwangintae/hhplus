package org.hhplus.ecommerce.cash.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hhplus.ecommerce.cash.event.PaySuccessEvent;
import org.hhplus.ecommerce.cash.infra.jpa.CashOutbox;
import org.hhplus.ecommerce.cash.infra.jpa.CashOutboxJpaRepository;
import org.hhplus.ecommerce.item.event.SubStockSuccessEvent;
import org.hhplus.ecommerce.item.infra.jpa.StockOutbox;
import org.hhplus.ecommerce.item.infra.jpa.StockOutboxJpaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CashKafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private final CashOutboxJpaRepository cashOutboxJpaRepository;

    @Transactional
    @KafkaListener(groupId = "cash-pay-test-group", topics = "cash-pay-topic")
    public void cashPayTestGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            PaySuccessEvent event = objectMapper.readValue(jsonPayload, PaySuccessEvent.class);

            CashOutbox cashOutbox = cashOutboxJpaRepository.findByOrderId(event.getOrderId())
                    .orElseThrow(() -> new RuntimeException("결제된 주문을 찾을 수 없습니다."));

            cashOutbox.published();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("결제 outbox 변경에 실패 했습니다.");
        }

    }
}
