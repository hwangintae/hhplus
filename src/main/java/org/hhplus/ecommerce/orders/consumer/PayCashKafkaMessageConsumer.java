package org.hhplus.ecommerce.orders.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hhplus.ecommerce.cash.event.PaySuccessEvent;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PayCashKafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private final OrdersService ordersService;

    @KafkaListener(groupId = "cash-pay-group", topics = "cash-pay-topic")
    public void cashPayGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            PaySuccessEvent event = objectMapper.readValue(jsonPayload, PaySuccessEvent.class);

            List<Long> orderItemIds = event.getOrderItemIds();

            ordersService.orderSuccess(orderItemIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("결제 outbox 변경에 실패 했습니다.");
        }

    }
}
