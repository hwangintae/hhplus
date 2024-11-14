package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.orders.infra.jpa.Orders;
import org.hhplus.ecommerce.orders.usecase.OrderStatus;

@Getter
public class OrdersDomain {

    private final Long id;
    private final Long userId;

    @Builder
    protected OrdersDomain(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public static OrdersDomain generateOrdersDomain(Long userId) {
        return OrdersDomain.builder()
                .userId(userId)
                .build();
    }

    public Orders toEntity() {
        return Orders.builder()
                .id(this.id)
                .userId(this.userId)
                .build();
    }
}
