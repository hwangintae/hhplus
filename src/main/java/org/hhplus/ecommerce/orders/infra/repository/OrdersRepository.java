package org.hhplus.ecommerce.orders.infra.repository;

import org.hhplus.ecommerce.orders.infra.jpa.Orders;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository {
    List<Orders> findByUserId(Long userId);
    Orders findById(Long id);
    Orders save(Orders orders);

}
