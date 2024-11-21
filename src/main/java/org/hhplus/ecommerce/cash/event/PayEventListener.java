package org.hhplus.ecommerce.cash.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.infra.jpa.CashOutbox;
import org.hhplus.ecommerce.cash.infra.jpa.CashOutboxJpaRepository;
import org.hhplus.ecommerce.cash.infra.jpa.CashOutboxStatus;
import org.hhplus.ecommerce.cash.infra.producer.CashKafkaMessagePublisher;
import org.hhplus.ecommerce.common.SlackWebhook;
import org.hhplus.ecommerce.item.event.SubStockSuccessEvent;
import org.hhplus.ecommerce.item.infra.jpa.StockOutbox;
import org.hhplus.ecommerce.item.infra.jpa.StockOutboxStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PayEventListener {

    private final ObjectMapper objectMapper;
    private final CashOutboxJpaRepository cashOutboxJpaRepository;
    private final CashKafkaMessagePublisher cashKafkaMessagePublisher;
    private final SlackWebhook slackWebhook;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void payEventSuccessHandler(PaySuccessEvent event) {

        try {
            String jsonString = objectMapper.writeValueAsString(event);
            cashOutboxJpaRepository.save(CashOutbox.init(event.getOrderId(), jsonString));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void payEventKafkaPublish(PaySuccessEvent event) {
        cashKafkaMessagePublisher.publish(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void payEventKafkaPublish(PayFailEvent event) {
        cashKafkaMessagePublisher.publish(event);

    }

    @Scheduled(fixedRate = 5000)
    public void retryKafkaMessagePublish() {
        List<CashOutbox> cashOutboxes = cashOutboxJpaRepository.findByStatus(CashOutboxStatus.INIT);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        cashOutboxes.forEach(outbox -> {
            if (outbox.getCreatedAt().isBefore(fiveMinutesAgo)) {
                String jsonString = outbox.getJsonString();

                try {
                    PaySuccessEvent paySuccessEvent = objectMapper.readValue(jsonString, PaySuccessEvent.class);

                    cashKafkaMessagePublisher.publish(paySuccessEvent);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                slackWebhook.send("retryKafkaMessagePublish CashOutbox");
            }
        });

    }
}
