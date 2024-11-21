package org.hhplus.ecommerce.item.infra.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.common.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;

    @Column(columnDefinition = "TEXT")
    private String jsonString;

    @Enumerated(EnumType.STRING)
    private StockOutboxStatus status;

    @Builder
    protected StockOutbox(Long id, Long orderId, String jsonString, StockOutboxStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.jsonString = jsonString;
        this.status = status;
    }

    public static StockOutbox init(Long orderId, String jsonString) {
        return StockOutbox.builder()
                .orderId(orderId)
                .jsonString(jsonString)
                .status(StockOutboxStatus.INIT)
                .build();
    }

    public void published() {
        this.status = StockOutboxStatus.PUBLISHED;
    }
}
