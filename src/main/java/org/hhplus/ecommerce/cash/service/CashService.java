package org.hhplus.ecommerce.cash.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.infra.jpa.Cash;
import org.hhplus.ecommerce.cash.infra.jpa.CashHistory;
import org.hhplus.ecommerce.cash.infra.repository.CashHistoryRepository;
import org.hhplus.ecommerce.cash.infra.repository.CashRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CashDomain addCash(CashRequest request) {

        CashDomain cashDomain = cashRepository.findByUserId(request.getUserId())
                .toDomain();

        cashDomain.add(request.getAmount());

        Cash cash = cashRepository.save(cashDomain.toEntity());

        CashHistory cashHistory = CashHistory.generateChargeCashHistory(cash.getId(), request.getAmount());
        cashHistoryRepository.save(cashHistory);

        return cash.toDomain();
    }

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
