package org.hhplus.ecommerce.orders.usecase;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.dataPlatform.DataPlatformService;
import org.hhplus.ecommerce.item.service.ItemDomain;
import org.hhplus.ecommerce.item.service.ItemService;
import org.hhplus.ecommerce.item.service.StockService;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrdersFacade {

    private final OrdersService ordersService;
    private final CashService cashService;
    private final ItemService itemService;
    private final StockService stockService;
    private final DataPlatformService dataPlatformService;

    @Transactional
    public List<OrderItemDomain> createOrder(Long userId, List<OrderRequest> orderRequests) {

        // 주문할 상품별 구매 개수 map(itemId, cnt)
        Map<Long, Integer> orderReqeustsMap = orderRequests.stream()
                .collect(Collectors.toMap(OrderRequest::getItemId, OrderRequest::getCnt));

        // 상품 id 목록
        List<Long> itemIds = orderRequests.stream()
                .map(OrderRequest::getItemId)
                .toList();

        // 상품 ID, 상품 명, 상품 가격, 재고 목록
        List<ItemDomain> itemDomains = itemService.getItems(itemIds);

        // 구매가 가능한 상품 목록
        // 재고가 있는지 확인
        List<ItemDomain> notOverQuantityItemDomains = itemDomains.stream()
                .filter(itemDomain -> {
                    Long itemId = itemDomain.getId();
                    int cnt = orderReqeustsMap.getOrDefault(itemId, -Integer.MAX_VALUE);

                    return stockService.checkStock(itemId, cnt);
                })
                .toList();

        // 재고 차감
        notOverQuantityItemDomains.forEach(item -> {
                    Long itemId = item.getId();
                    int cnt = orderReqeustsMap.getOrDefault(itemId, 0);

                    stockService.subStock(itemId, cnt);
                });



        // 구매 가능한 상품 총 가격
        long totalPrice = notOverQuantityItemDomains.stream()
                .mapToLong(itemDomain -> {
                    Long itemId = itemDomain.getId();
                    long price = itemDomain.getPrice();
                    int cnt = orderReqeustsMap.getOrDefault(itemId, 0);

                    return price * cnt;
                })
                .sum();

        CashRequest cashRequest = CashRequest.builder()
                .userId(userId)
                .amount(totalPrice)
                .build();

        // 상품 결제
        cashService.subCash(cashRequest);

        // 주문 상품 목록
        List<OrderRequest> realOrderRequests = notOverQuantityItemDomains.stream()
                .map(itemDomain -> {
                    Long itemId = itemDomain.getId();
                    int cnt = orderReqeustsMap.getOrDefault(itemId, 0);

                    return OrderRequest.builder()
                            .itemId(itemId)
                            .cnt(cnt)
                            .build();
                })
                .filter(orderRequest -> orderRequest.getCnt() != 0)
                .toList();

        // 주문 상품 등록
        List<OrderItemDomain> orders = ordersService.createOrders(userId, realOrderRequests);

        dataPlatformService.sendData(orders);

        return orders;
    }
}
