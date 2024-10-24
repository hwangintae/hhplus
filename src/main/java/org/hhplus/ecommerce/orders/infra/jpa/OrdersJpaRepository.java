package org.hhplus.ecommerce.orders.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersJpaRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserId(Long userId);
}
