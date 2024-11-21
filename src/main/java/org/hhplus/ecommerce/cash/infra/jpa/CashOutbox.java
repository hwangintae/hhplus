package org.hhplus.ecommerce.cash.infra.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.common.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CashOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;

    @Column(columnDefinition = "TEXT")
    private String jsonString;

    @Enumerated(EnumType.STRING)
    private CashOutboxStatus status;

    @Builder
    protected CashOutbox(Long id, Long orderId, String jsonString, CashOutboxStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.jsonString = jsonString;
        this.status = status;
    }

    public static CashOutbox init(Long orderId, String jsonString) {
        return CashOutbox.builder()
                .orderId(orderId)
                .jsonString(jsonString)
                .status(CashOutboxStatus.INIT)
                .build();
    }

    public void published() {
        this.status = CashOutboxStatus.PUBLISHED;
    }
}
