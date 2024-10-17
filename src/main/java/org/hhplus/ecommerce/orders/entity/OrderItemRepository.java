package org.hhplus.ecommerce.orders.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.createdAt >= :startDateTime and oi.createdAt < :endDateTime ORDER BY oi.itemCnt DESC")
    List<OrderItem> findTopFiveDuringThreeDays(@Param("startDateTime") LocalDateTime startDateTime,
                                               @Param("endDateTime") LocalDateTime endDateTime);

    List<OrderItem> findByOrdersIdIn(List<Long> ordersId);
}
