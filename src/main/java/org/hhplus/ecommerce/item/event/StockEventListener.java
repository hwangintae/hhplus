package org.hhplus.ecommerce.item.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.SlackWebhook;
import org.hhplus.ecommerce.item.infra.jpa.StockOutbox;
import org.hhplus.ecommerce.item.infra.jpa.StockOutboxJpaRepository;
import org.hhplus.ecommerce.item.infra.jpa.StockOutboxStatus;
import org.hhplus.ecommerce.item.infra.producer.StockKafkaMessagePublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StockEventListener {

    private final StockOutboxJpaRepository stockOutboxJpaRepository;
    private final ObjectMapper objectMapper;
    private final StockKafkaMessagePublisher stockKafkaMessagePublisher;
    private final SlackWebhook slackWebhook;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void subStockEventSuccessHandler(SubStockSuccessEvent event) {

        try {
            String jsonString = objectMapper.writeValueAsString(event);
            StockOutbox stockOutbox = StockOutbox.init(event.getOrderId(), jsonString);

            stockOutboxJpaRepository.save(stockOutbox);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void subStockEventKafkaPublish(SubStockSuccessEvent event) {
        stockKafkaMessagePublisher.publish(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void subStockFailEventKafkaPublish(SubStockFailEvent event) {
        stockKafkaMessagePublisher.publish(event);
    }

    @Scheduled(fixedRate = 5000)
    public void retryKafkaMessagePublish() {
        List<StockOutbox> stockOutboxes = stockOutboxJpaRepository.findByStatus(StockOutboxStatus.INIT);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        stockOutboxes.forEach(outbox -> {
            if (outbox.getCreatedAt().isBefore(fiveMinutesAgo)) {
                String jsonString = outbox.getJsonString();

                try {
                    SubStockSuccessEvent subStockSuccessEvent = objectMapper.readValue(jsonString, SubStockSuccessEvent.class);

                    stockKafkaMessagePublisher.publish(subStockSuccessEvent);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                slackWebhook.send("retryKafkaMessagePublish StockOutbox");
            }
        });

    }
}
