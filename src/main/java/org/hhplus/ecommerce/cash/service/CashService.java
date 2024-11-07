package org.hhplus.ecommerce.cash.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistory;
import org.hhplus.ecommerce.cash.infra.repository.CashHistoryRepository;
import org.hhplus.ecommerce.cash.infra.repository.CashRepository;
import org.hhplus.ecommerce.common.redis.DistributedLock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CashService {

    private final CashRepository cashRepository;
    private final CashHistoryRepository cashHistoryRepository;

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
    public CashDomain subCash(CashRequest request) {

        CashDomain cashDomain = cashRepository.findByUserId(request.getUserId())
                .toDomain();

        cashDomain.sub(request.getAmount());

        Cash cash = cashRepository.save(cashDomain.toEntity());

        CashHistory cashHistory = CashHistory.generateUseCashHistory(cash.getId(), request.getAmount());
        cashHistoryRepository.save(cashHistory);

        return cash.toDomain();
    }
}
