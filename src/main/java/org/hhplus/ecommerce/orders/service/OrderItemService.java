package org.hhplus.ecommerce.orders.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.orders.infra.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void createOrderItem() {

    }
}
