package org.hhplus.ecommerce.cash.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.item.event.SubStockSuccessEvent;
import org.hhplus.ecommerce.item.service.ItemInfo;
import org.hhplus.ecommerce.item.usecase.ItemFacade;
import org.hhplus.ecommerce.orders.event.OrderSuccessEvent;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubStockKafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private final CashService cashService;

    @KafkaListener(groupId = "sub-stock-group", topics = "sub-stock-topic")
    public void subStockGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            SubStockSuccessEvent event = objectMapper.readValue(jsonPayload, SubStockSuccessEvent.class);

            List<OrderItemInfo> orderItemInfos = event.getOrderItemInfos();

            Long amount = event.getItemInfos().stream()
                    .map(ItemInfo::getPrice)
                    .reduce(0L, Long::sum);

            CashRequest cashRequest = CashRequest.builder()
                    .userId(event.getUserId())
                    .orderItemInfos(orderItemInfos)
                    .amount(amount)
                    .build();

            cashService.pay(event.getOrderId(), cashRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
