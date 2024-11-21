package org.hhplus.ecommerce.cash.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ecommerce.cash.event.PayFailEvent;
import org.hhplus.ecommerce.cash.event.PaySuccessEvent;
import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistory;
import org.hhplus.ecommerce.cash.infra.repository.CashHistoryRepository;
import org.hhplus.ecommerce.cash.infra.repository.CashRepository;
import org.hhplus.ecommerce.common.redis.DistributedLock;
import org.hhplus.ecommerce.orders.service.OrderItemInfo;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.INSUFFICIENT_USER_CASH;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashService {

    private final CashRepository cashRepository;
    private final CashHistoryRepository cashHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CashDomain getCash(Long userId) {
        return cashRepository.findByUserId(userId)
                .toDomain();
    }

    @DistributedLock(prefix = "cash", key = "#request.getUserId()")
    @Transactional
    public CashDomain addCash(CashRequest request) {
        log.info(">>> addCashAop start");

        CashDomain cashDomain = cashRepository.findByUserId(request.getUserId())
                .toDomain();

        cashDomain.add(request.getAmount());

        Cash cash = cashRepository.save(cashDomain.toEntity());

        CashHistory cashHistory = CashHistory.generateChargeCashHistory(cash.getId(), request.getAmount());
        cashHistoryRepository.save(cashHistory);

        log.info(">>> addCashAop end");
        return cash.toDomain();
    }

    @DistributedLock(prefix = "cash", key = "#request.getUserId()")
    @Transactional
    public CashDomain pay(Long orderId, CashRequest request) {

        CashDomain cashDomain = cashRepository.findByUserId(request.getUserId())
                .toDomain();

        try {
            cashDomain.sub(request.getAmount());
        } catch (Exception e) {
            eventPublisher.publishEvent(new PayFailEvent(orderId, request.getOrderItemInfos()));
            throw new IllegalArgumentException(INSUFFICIENT_USER_CASH.getMessage());
        }

        Cash cash = cashRepository.save(cashDomain.toEntity());

        CashHistory cashHistory = CashHistory.generateUseCashHistory(cash.getId(), request.getAmount());
        cashHistoryRepository.save(cashHistory);

        List<Long> orderItemIds = request.getOrderItemInfos().stream()
                .map(OrderItemInfo::getOrderItemId)
                .toList();

        eventPublisher.publishEvent(new PaySuccessEvent(orderId, orderItemIds));

        return cash.toDomain();
    }
}
