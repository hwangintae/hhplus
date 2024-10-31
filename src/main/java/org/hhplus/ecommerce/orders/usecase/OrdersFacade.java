package org.hhplus.ecommerce.orders.usecase;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.common.DistributedLock;
import org.hhplus.ecommerce.dataPlatform.DataPlatformService;
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
    private final DataPlatformService dataPlatformService;
    private final DistributedLock distributedLock;
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

        // 구매가 가능한 상품 목록
        // 재고가 있는지 확인
//        List<ItemDomain> notOverQuantityItemDomains = itemDomains.stream()
//                .filter(itemDomain -> {
//                    Long itemId = itemDomain.getId();
//                    int cnt = orderReqeustsMap.getOrDefault(itemId, -Integer.MAX_VALUE);
//
//                    return stockService.checkStock(itemId, cnt);
//                })
//                .toList();

        // 재고 차감
        List<TmpItemDomainAndCnt> tmpItemDomainAndCnts = new ArrayList<>();
        for (ItemDomain item : itemDomains) {
            Long itemId = item.getId();
            int cnt = orderReqeustsMap.getOrDefault(itemId, 0);

            try {
                distributedLock.redissonLock("stock:" + itemId, () -> stockService.subStock(itemId, cnt));

                tmpItemDomainAndCnts.add(new TmpItemDomainAndCnt(item, cnt));
            } catch (Exception e) {
                tmpItemDomainAndCnts.forEach(itemDomainAndCnt -> {
                    Long tmpItemId = itemDomainAndCnt.getItemDomain().getId();
                    distributedLock.redissonLock("stock:" + tmpItemId, () -> restoreStockService.addStock(tmpItemId, itemDomainAndCnt.getCnt()));
                });

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
            distributedLock.redissonLock("cash:" + cashRequest.getUserId(),() -> cashService.subCash(cashRequest));
        } catch (Exception e) {
            tmpItemDomainAndCnts.forEach(itemDomainAndCnt -> {
                Long tmpItemId = itemDomainAndCnt.getItemDomain().getId();
                distributedLock.redissonLock("stock:" + tmpItemId, () -> restoreStockService.addStock(tmpItemId, itemDomainAndCnt.getCnt()));
            });
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
        List<OrderItemDomain> orders = ordersService.createOrders(userId, realOrderRequests);

        dataPlatformService.sendData(orders);

        return orders;
    }
}
