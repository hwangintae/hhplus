package org.hhplus.ecommerce.item.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.redis.DistributedLock;
import org.hhplus.ecommerce.item.event.SubStockFailEvent;
import org.hhplus.ecommerce.item.event.SubStockSuccessEvent;
import org.hhplus.ecommerce.item.infra.jpa.Stock;
import org.hhplus.ecommerce.item.infra.repository.StockRepository;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {

    private final StockRepository stockRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public StockDomain getStock(Long itemId) {
        return stockRepository.findByItemId(itemId)
                .toDomain();
    }

    @Transactional
    public List<StockDomain> getStocks(List<Long> itemIds) {
        List<Stock> stocks = stockRepository.findByItemIdIn(itemIds);

        return stocks.stream()
                .map(Stock::toDomain)
                .toList();
    }

    @DistributedLock(prefix = "stock", key = "#itemId")
    @Transactional
    public StockDomain subStock(Long itemId, int cnt) {

        StockDomain stockDomain = stockRepository.findByItemId(itemId)
                .toDomain();

        stockDomain.subQuantity(cnt);

        return stockRepository.save(stockDomain.toEntity())
                .toDomain();
    }

    @DistributedLock(prefix = "stock", key = "#orderItemInfos.![itemId]")
    @Transactional
    public List<StockDomain> subStocks(Long userId, Long orderId, List<OrderItemInfo> orderItemInfos, List<ItemDomain> itemDomains) {

        // 재고 차감 item 찾기
        List<Long> itemIds = orderItemInfos.stream()
                .map(OrderItemInfo::getItemId)
                .toList();

        List<Stock> stocks = stockRepository.findByItemIdIn(itemIds);

        List<StockDomain> stockDomains = stocks.stream()
                .map(Stock::toDomain)
                .toList();

        // 상품 별 재고 차감 개수
        Map<Long, Integer> orderItemInfoMap = orderItemInfos.stream()
                .collect(Collectors.toMap(
                        OrderItemInfo::getItemId,
                        OrderItemInfo::getItemCnt
                ));

        // 재고 차감
        try {
            stockDomains.forEach(stockDomain -> {
                stockDomain.subQuantity(orderItemInfoMap.get(stockDomain.getItemId()));
            });
        } catch (Exception ignore) {
            eventPublisher.publishEvent(new SubStockFailEvent(userId, orderItemInfos));
        }

        List<Stock> results = stockRepository.saveAll(stockDomains.stream()
                .map(StockDomain::toEntity)
                .toList());

        List<ItemInfo> itemInfos = itemDomains.stream()
                .map(ItemDomain::toInfo)
                .toList();

        // 재고 차감 성공 이밴트 발송 -> 결제 해라!
        eventPublisher.publishEvent(new SubStockSuccessEvent(userId, orderId, orderItemInfos, itemInfos));

        return results.stream()
                .map(Stock::toDomain)
                .toList();
    }
}
