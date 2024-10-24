package org.hhplus.ecommerce.orders.infra.jpa;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.ecommerce.orders.usecase.OrderStatus;
import org.hhplus.ecommerce.orders.service.OrdersDomain;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder
    protected Orders(Long id, Long userId, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.status = status;
    }

    public OrdersDomain toDomain() {
        return OrdersDomain.builder()
                .id(this.id)
                .userId(this.userId)
                .status(this.status)
                .build();
    }
}
