package org.hhplus.ecommerce.item.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItemId(Long itemId);
    List<Stock> findByItemIdIn(List<Long> itemIds);
}
