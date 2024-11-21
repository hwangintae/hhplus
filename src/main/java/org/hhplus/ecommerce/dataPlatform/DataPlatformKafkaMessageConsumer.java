package org.hhplus.ecommerce.dataPlatform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hhplus.ecommerce.orders.event.OrderPaySuccessEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataPlatformKafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private final DataPlatformService dataPlatformService;

    @KafkaListener(groupId = "order-pay-success-group", topics = "order-pay-success-topic")
    public void subStockGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            OrderPaySuccessEvent event = objectMapper.readValue(jsonPayload, OrderPaySuccessEvent.class);

            dataPlatformService.sendData(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
