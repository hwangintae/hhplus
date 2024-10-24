package org.hhplus.ecommerce.orders.infra.repository;

import org.hhplus.ecommerce.orders.infra.jpa.Orders;

import java.util.List;

public interface OrdersRepository {
    List<Orders> findByUserId(Long userId);
    Orders save(Orders orders);

}
