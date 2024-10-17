package org.hhplus.ecommerce.orders.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TopOrderItemRepository extends JpaRepository<TopOrderItem, Long> {
    List<TopOrderItem> findByCreateDay(LocalDate createDay);
}
