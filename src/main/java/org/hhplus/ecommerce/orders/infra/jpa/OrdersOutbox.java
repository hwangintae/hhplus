package org.hhplus.ecommerce.orders.infra.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.common.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrdersOutbox extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @Column(columnDefinition = "TEXT")
    private String jsonString;

    @Enumerated(EnumType.STRING)
    private OrdersOutboxStatus status;

    @Builder
    protected OrdersOutbox(Long id, Long orderId, String jsonString, OrdersOutboxStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.jsonString = jsonString;
        this.status = status;
    }

    public static OrdersOutbox init(Long orderId, String jsonString) {
        return OrdersOutbox.builder()
                .orderId(orderId)
                .jsonString(jsonString)
                .status(OrdersOutboxStatus.INIT)
                .build();
    }

    public void published() {
        this.status = OrdersOutboxStatus.PUBLISHED;
    }
}
