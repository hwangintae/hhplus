package org.hhplus.ecommerce.orders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ecommerce.orders.event.OrderingSuccessEvent;
import org.hhplus.ecommerce.orders.event.OrdersEventPublisher;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItem;
import org.hhplus.ecommerce.orders.infra.jpa.Orders;
import org.hhplus.ecommerce.orders.infra.repository.OrderItemRepository;
import org.hhplus.ecommerce.orders.infra.repository.OrdersRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrdersEventPublisher ordersEventPublisher;

    public List<OrderItemDomain> getOrders(Long userId) {
        List<Orders> orders = ordersRepository.findByUserId(userId);

        List<Long> ordersIds = orders.stream()
                .map(Orders::getId)
                .toList();

        List<OrderItem> orderItems = orderItemRepository.findByOrdersIdIn(ordersIds);
        return orderItems.stream()
                .filter(item -> !item.isDeleteAt())
                .map(OrderItem::toDomain)
                .toList();
    }

    @Transactional
    public List<OrderItemDomain> createOrders(Long userId, List<OrderRequest> orderRequests) {

        OrdersDomain ordersDomain = OrdersDomain.generateOrdersDomain(userId);

        OrdersDomain saveOrdersDomain = ordersRepository.save(ordersDomain.toEntity()).toDomain();

        List<OrderItemDomain> orderItemDomains = orderRequests.stream()
                .map(request -> OrderItemDomain.generateOrderItemDomain(
                        saveOrdersDomain.getId(),
                        request.getItemId(),
                        request.getCnt()))
                .toList();

        List<OrderItem> orderItems = orderItemDomains.stream()
                .map(OrderItemDomain::toEntity)
                .toList();

        List<OrderItem> saves = orderItemRepository.save(orderItems);

        List<OrderItemDomain> results = saves.stream()
                .map(OrderItem::toDomain)
                .toList();

        List<OrderItemRequest> orderItemRequests = results.stream()
                .map(OrderItemDomain::toRequest)
                .toList();

        ordersEventPublisher.success(new OrderingSuccessEvent(orderItemRequests));

        return results;
    }

    @Cacheable(value = "popularItemsCache", key = "#from + '_' + #limit", cacheManager = "redisCacheManager")
    public List<PopularItemsResult> getPopularItems(int from, int limit) {
        return orderItemRepository.findPopularItems(from, limit);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @CacheEvict(value = "popularItemsCache", allEntries = true)
    public void clearPopularItemsCache() {
        log.info("clear Popular Items Cache");
    }
}
