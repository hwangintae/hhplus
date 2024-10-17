package org.hhplus.ecommerce.orders.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.entity.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final TopOrderItemRepository topOrderItemRepository;

    @Transactional
    public List<OrderItemDomain> createOrders(Long userId, List<OrderRequest> orderRequests) {

        OrdersDomain ordersDomain = OrdersDomain.generateOrdersDomain(userId);

        Orders orders = ordersRepository.save(ordersDomain.toEntity());

        List<OrderItemDomain> orderItemDomains = orderRequests.stream()
                .map(request -> OrderItemDomain.builder()
                        .ordersId(orders.getId())
                        .itemId(request.getItemId())
                        .itemCnt(request.getCnt())
                        .build())
                .toList();

        List<OrderItem> orderItems = orderItemRepository.saveAll(orderItemDomains.stream()
                .map(OrderItemDomain::toEntity)
                .toList());

        return orderItems.stream()
                .map(OrderItem::toDomain)
                .toList();
    }

    public List<TopOrderItemDomain> getTopFiveOrderItems(LocalDate today) {
        List<TopOrderItem> topOrderItems = topOrderItemRepository.findByDate(today);

        return topOrderItems.stream()
                .map(TopOrderItem::toDomain)
                .sorted(Comparator.comparing(TopOrderItemDomain::getCnt).reversed()
                        .thenComparing(TopOrderItemDomain::getId))
                .toList();
    }

    @Scheduled(cron = "1 0 0 * * *")
    public void createTopFiveOrderItem() {

        LocalDate now = LocalDate.now();
        LocalDateTime endDateTime = now.atTime(LocalTime.MIDNIGHT);
        LocalDateTime startDateTime = endDateTime.minusDays(3);

        List<OrderItem> topFiveDuringThreeDays = orderItemRepository.findTop5DuringThreeDays(startDateTime, endDateTime);

        List<TopOrderItemDomain> resultDomains = topFiveDuringThreeDays.stream()
                .map(item -> TopOrderItemDomain.builder()
                        .createDay(now)
                        .itemId(item.getItemId())
                        .cnt(item.getItemCnt())
                        .build())
                .toList();

        List<TopOrderItem> result = resultDomains.stream()
                .map(TopOrderItemDomain::toEntity)
                .toList();

        topOrderItemRepository.saveAll(result);
    }
}
