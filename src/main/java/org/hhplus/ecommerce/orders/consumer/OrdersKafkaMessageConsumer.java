package org.hhplus.ecommerce.orders.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hhplus.ecommerce.cash.event.PayFailEvent;
import org.hhplus.ecommerce.item.event.SubStockFailEvent;
import org.hhplus.ecommerce.orders.event.OrderPaySuccessEvent;
import org.hhplus.ecommerce.orders.event.OrderSuccessEvent;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersDataPlatformOutbox;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersDataPlatformOutboxJpaRepository;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersOutbox;
import org.hhplus.ecommerce.orders.infra.jpa.OrdersOutboxJpaRepository;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrdersKafkaMessageConsumer {

    private final ObjectMapper objectMapper;
    private final OrdersOutboxJpaRepository ordersOutboxJpaRepository;
    private final OrdersDataPlatformOutboxJpaRepository ordersDataPlatformOutboxJpaRepository;
    private final OrdersService ordersService;

    @Transactional
    @KafkaListener(groupId = "order-test-group", topics = "order-init-topic")
    public void orderTestGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            OrderSuccessEvent event = objectMapper.readValue(jsonPayload, OrderSuccessEvent.class);

            OrdersOutbox ordersOutbox = ordersOutboxJpaRepository.findByOrderId(event.getOrderId())
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

            ordersOutbox.published();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    @KafkaListener(groupId = "order-pay-success-test-group", topics = "order-pay-success-topic")
    public void orderPayTestGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            OrderPaySuccessEvent event = objectMapper.readValue(jsonPayload, OrderPaySuccessEvent.class);

            OrdersDataPlatformOutbox ordersDataPlatformOutbox = ordersDataPlatformOutboxJpaRepository.findById(event.getOrderId())
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

            ordersDataPlatformOutbox.published();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @KafkaListener(groupId = "sub-stock-fail-group", topics = "sub-stock-fail-topic")
    public void subStockFailGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            SubStockFailEvent event = objectMapper.readValue(jsonPayload, SubStockFailEvent.class);

            List<OrderItemInfo> orderItemInfos = event.getOrderItemInfos();

            List<Long> orderItemIds = orderItemInfos.stream()
                    .map(OrderItemInfo::getOrderItemId)
                    .toList();

            ordersService.orderFail(orderItemIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @KafkaListener(groupId = "cash-pay-fail-order-group", topics = "cash-pay-fail-topic")
    public void cashPayFailGroupConsumer(ConsumerRecord<String, String> consumerRecord) {

        String jsonPayload = consumerRecord.value();

        try {
            PayFailEvent event = objectMapper.readValue(jsonPayload, PayFailEvent.class);

            List<OrderItemInfo> orderItemInfos = event.getOrderItemInfos();

            List<Long> orderItemIds = orderItemInfos.stream()
                    .map(OrderItemInfo::getOrderItemId)
                    .toList();

            ordersService.orderFail(orderItemIds);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
