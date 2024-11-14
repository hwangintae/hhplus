package org.hhplus.ecommerce.orders.usecase;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.item.service.*;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private final RestoreStockService restoreStockService;

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

        // 재고 차감
        List<TmpItemDomainAndCnt> tmpItemDomainAndCnts = new ArrayList<>();
        for (ItemDomain item : itemDomains) {
            Long itemId = item.getId();
            int cnt = orderReqeustsMap.getOrDefault(itemId, 0);

            try {
                stockService.subStock(itemId, cnt);

                tmpItemDomainAndCnts.add(new TmpItemDomainAndCnt(item, cnt));
            } catch (Exception ignore) {
            }
        }

        // 구매 가능한 상품 총 가격
        long totalPrice = tmpItemDomainAndCnts.stream()
                .mapToLong(item -> {
                    ItemDomain itemDomain = item.getItemDomain();

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
        try {
            cashService.subCash(cashRequest);
        } catch (Exception e) {
            tmpItemDomainAndCnts.forEach(itemDomainAndCnt -> {
                Long tmpItemId = itemDomainAndCnt.getItemDomain().getId();
                restoreStockService.addStock(tmpItemId, itemDomainAndCnt.getCnt());
            });

            throw new RuntimeException("상품 결제에 실패했어요.");
        }

        // 주문 상품 목록
        List<OrderRequest> realOrderRequests = tmpItemDomainAndCnts.stream()
                .map(item -> {
                    ItemDomain itemDomain = item.getItemDomain();

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
        return ordersService.createOrders(userId, realOrderRequests);
    }
}
