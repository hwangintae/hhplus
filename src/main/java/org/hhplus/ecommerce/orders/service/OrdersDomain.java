package org.hhplus.ecommerce.orders.service;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.ecommerce.orders.entity.Orders;
import org.hhplus.ecommerce.orders.usecase.OrderStatus;

@Getter
public class OrdersDomain {

    private final Long id;
    private final Long userId;
    private final OrderStatus status;

    @Builder
    protected OrdersDomain(Long id, Long userId, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.status = status;
    }

    public static OrdersDomain generateOrdersDomain(Long userId) {
        return OrdersDomain.builder()
                .userId(userId)
                .status(OrderStatus.SUCCESS)
                .build();
    }

    public Orders toEntity() {
        return Orders.builder()
                .id(this.id)
                .userId(this.userId)
                .status(this.status)
                .build();
    }
}
