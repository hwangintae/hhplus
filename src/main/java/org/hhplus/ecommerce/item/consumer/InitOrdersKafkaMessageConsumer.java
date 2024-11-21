package org.hhplus.ecommerce.item.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hhplus.ecommerce.item.usecase.ItemFacade;
import org.hhplus.ecommerce.orders.event.OrderSuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitOrdersKafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private final ItemFacade itemFacade;

    @KafkaListener(groupId = "order-group", topics = "order-init-topic")
    public void orderGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            OrderSuccessEvent event = objectMapper.readValue(jsonPayload, OrderSuccessEvent.class);

            itemFacade.subStockItem(event.getUserId(), event.getOrderId(), event.getOrderItemInfos());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
