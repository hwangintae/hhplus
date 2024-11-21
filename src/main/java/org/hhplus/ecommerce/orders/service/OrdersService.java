package org.hhplus.ecommerce.orders.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ecommerce.orders.event.OrderPaySuccessEvent;
import org.hhplus.ecommerce.orders.event.OrderSuccessEvent;
import org.hhplus.ecommerce.orders.infra.jpa.OrderItem;
import org.hhplus.ecommerce.orders.infra.jpa.Orders;
import org.hhplus.ecommerce.orders.infra.repository.OrderItemRepository;
import org.hhplus.ecommerce.orders.infra.repository.OrdersRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

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

    public List<OrderItemDomain> getOrderItems(Long userId) {
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

    public List<OrderItemDomain> getOrderItemsByOrderId(Long ordersId) {

        List<OrderItem> orderItems = orderItemRepository.findByOrdersId(ordersId);
        return orderItems.stream()
                .filter(item -> !item.isDeleteAt())
                .map(OrderItem::toDomain)
                .toList();
    }



    // 주문 중으로 등록
    @Transactional
    public List<OrderItemDomain> createOrders(Long userId, List<OrderRequest> orderRequests) {

        // 주문 저장
        OrdersDomain ordersDomain = OrdersDomain.generateOrdersDomain(userId);
        OrdersDomain saveOrdersDomain = ordersRepository.save(ordersDomain.toEntity()).toDomain();

        // 주문 아이템 저장
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

        // 주문 요청 상태
        List<OrderItemDomain> results = saves.stream()
                .map(OrderItem::toDomain)
                .toList();

        List<OrderItemInfo> orderItemInfos = results.stream()
                .map(OrderItemDomain::toInfo)
                .toList();

        // 주문 요청 성공 이벤트 발행 -> 재고 차감 하세요!
        eventPublisher.publishEvent(new OrderSuccessEvent(userId, saveOrdersDomain.getId(), orderItemInfos));

        return results;
    }

    @Transactional
    public void orderSuccess(List<Long> orderItemIds) {
        List<OrderItem> findOrderItemIds = orderItemRepository.findById(orderItemIds);

        List<OrderItemDomain> orderItemDomains = findOrderItemIds.stream()
                .map(OrderItem::toDomain)
                .toList();

        orderItemDomains.forEach(OrderItemDomain::orderSuccess);

        orderItemRepository.save(orderItemDomains.stream()
                .map(OrderItemDomain::toEntity)
                .toList());

        // 주문 결제 성공 이벤트 발행 -> 외부 api 호출!
        Long orderId = orderItemDomains.get(0).getOrdersId();
        Orders orders = ordersRepository.findById(orderId);

        List<OrderItemInfo> orderItemInfos = orderItemDomains.stream()
                .map(OrderItemDomain::toInfo)
                .toList();

        eventPublisher.publishEvent(new OrderPaySuccessEvent(orders.getUserId(), orderId, orderItemInfos));
    }

    @Transactional
    public void orderFail(List<Long> orderItemIds) {
        List<OrderItem> findOrderItemIds = orderItemRepository.findById(orderItemIds);

        List<OrderItemDomain> orderItemDomains = findOrderItemIds.stream()
                .map(OrderItem::toDomain)
                .toList();

        orderItemDomains.forEach(OrderItemDomain::orderFail);

        orderItemRepository.save(orderItemDomains.stream()
                .map(OrderItemDomain::toEntity)
                .toList());
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
