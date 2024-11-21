package org.hhplus.ecommerce.orders.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.SlackWebhook;
import org.hhplus.ecommerce.orders.infra.jpa.*;
import org.hhplus.ecommerce.orders.infra.producer.OrdersKafkaMessagePublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrdersEventListener {

    private final ObjectMapper objectMapper;
    private final OrdersKafkaMessagePublisher ordersKafkaMessagePublisher;
    private final OrdersOutboxJpaRepository ordersOutboxJpaRepository;
    private final OrdersDataPlatformOutboxJpaRepository ordersDataPlatformOutboxJpaRepository;
    private final SlackWebhook slackWebhook;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void orderInitEventSuccessHandler(OrderSuccessEvent event) {

        try {
            String jsonString = objectMapper.writeValueAsString(event);
            ordersOutboxJpaRepository.save(OrdersOutbox.init(event.getOrderId(), jsonString));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void orderInitEventKafkaPublish(OrderSuccessEvent event) {
        ordersKafkaMessagePublisher.publish(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void orderInitEventSuccessHandler(OrderPaySuccessEvent event) {

        try {
            String jsonString = objectMapper.writeValueAsString(event);
            ordersDataPlatformOutboxJpaRepository.save(OrdersDataPlatformOutbox.init(event.getOrderId(), jsonString));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void orderPayEventKafkaPublish(OrderPaySuccessEvent event) {
        ordersKafkaMessagePublisher.publish(event);
    }

    @Scheduled(fixedRate = 5000)
    public void retryKafkaMessagePublish() {
        List<OrdersOutbox> ordersOutboxes = ordersOutboxJpaRepository.findByStatus(OrdersOutboxStatus.INIT);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        ordersOutboxes.forEach(outbox -> {
            if (outbox.getCreatedAt().isBefore(fiveMinutesAgo)) {
                String jsonString = outbox.getJsonString();

                try {
                    OrderSuccessEvent orderSuccessEvent = objectMapper.readValue(jsonString, OrderSuccessEvent.class);

                    ordersKafkaMessagePublisher.publish(orderSuccessEvent);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                slackWebhook.send("retryKafkaMessagePublish OrdersOutbox");
            }
        });

    }

    @Scheduled(fixedRate = 5000)
    public void retryDataPlatformKafkaMessagePublish() {
        List<OrdersDataPlatformOutbox> ordersDataPlatformOutboxes = ordersDataPlatformOutboxJpaRepository.findByStatus(OrdersOutboxStatus.INIT);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        ordersDataPlatformOutboxes.forEach(outbox -> {
            if (outbox.getCreatedAt().isBefore(fiveMinutesAgo)) {
                String jsonString = outbox.getJsonString();

                try {
                    OrderPaySuccessEvent orderPaySuccessEvent = objectMapper.readValue(jsonString, OrderPaySuccessEvent.class);

                    ordersKafkaMessagePublisher.publish(orderPaySuccessEvent);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                slackWebhook.send("retryKafkaMessagePublish OrdersDataPlatformOutbox");
            }
        });

    }
}
