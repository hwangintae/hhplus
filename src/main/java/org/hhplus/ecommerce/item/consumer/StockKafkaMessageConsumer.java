package org.hhplus.ecommerce.item.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hhplus.ecommerce.cash.event.PayFailEvent;
import org.hhplus.ecommerce.item.event.SubStockSuccessEvent;
import org.hhplus.ecommerce.item.infra.jpa.StockOutbox;
import org.hhplus.ecommerce.item.infra.jpa.StockOutboxJpaRepository;
import org.hhplus.ecommerce.item.service.RestoreStockService;
import org.hhplus.ecommerce.item.usecase.ItemFacade;
import org.hhplus.ecommerce.orders.event.OrderSuccessEvent;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StockKafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private final StockOutboxJpaRepository stockOutboxJpaRepository;
    private final RestoreStockService restoreStockService;

    @Transactional
    @KafkaListener(groupId = "sub-stock-test-group", topics = "sub-stock-topic")
    public void subStockTestGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            SubStockSuccessEvent event = objectMapper.readValue(jsonPayload, SubStockSuccessEvent.class);

            StockOutbox stockOutbox = stockOutboxJpaRepository.findByOrderId(event.getOrderId())
                    .orElseThrow(() -> new RuntimeException("재고 차감된 주문을 찾을 수 없습니다."));

            stockOutbox.published();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @KafkaListener(groupId = "cash-pay-fail-group", topics = "cash-pay-fail-topic")
    public void cashPayFailGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            PayFailEvent event = objectMapper.readValue(jsonPayload, PayFailEvent.class);

            List<OrderItemInfo> orderItemInfos = event.getOrderItemInfos();

            orderItemInfos.forEach(item -> {
                restoreStockService.addStock(item.getItemId(), item.getItemCnt());
            });

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
