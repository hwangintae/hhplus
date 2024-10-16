package org.hhplus.ecommerce.orders.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.entity.OrderItem;
import org.hhplus.ecommerce.orders.entity.OrderItemRepository;
import org.hhplus.ecommerce.orders.entity.Orders;
import org.hhplus.ecommerce.orders.entity.OrdersRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public List<OrderItemDomain> createOrders(Long userId, List<OrderRequest> orderRequests) {

        OrdersDomain ordersDomain = OrdersDomain.generateOrdersDomain(userId);

        Orders orders = ordersRepository.save(Orders.of(ordersDomain));

        List<OrderItemDomain> orderItemDomains = orderRequests.stream()
                .map(request -> OrderItemDomain.builder()
                        .ordersId(orders.getId())
                        .itemId(request.getItemId())
                        .itemCnt(request.getCnt())
                        .build())
                .toList();

        List<OrderItem> orderItems = orderItemRepository.saveAll(orderItemDomains.stream()
                .map(OrderItem::of)
                .toList());

        return orderItems.stream()
                .map(OrderItem::toDomain)
                .toList();
    }
}
