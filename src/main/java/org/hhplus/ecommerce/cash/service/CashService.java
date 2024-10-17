package org.hhplus.ecommerce.cash.service;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.entity.Cash;
import org.hhplus.ecommerce.cash.entity.CashHistory;
import org.hhplus.ecommerce.cash.entity.CashHistoryRepository;
import org.hhplus.ecommerce.cash.entity.CashRepository;
import org.hhplus.ecommerce.common.exception.EcommerceBadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.hhplus.ecommerce.common.exception.EcommerceErrors.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CashService {

    private final CashRepository cashRepository;
    private final CashHistoryRepository cashHistoryRepository;

    @Transactional
    public CashDomain getCash(Long userId) {
        return cashRepository.findByUserId(userId)
                .orElseThrow(() -> new EcommerceBadRequestException(USER_NOT_FOUND))
                .toDomain();
    }

    @Transactional
    public CashDomain addCash(CashRequest request) {

        CashDomain cashDomain = cashRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new EcommerceBadRequestException(USER_NOT_FOUND))
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
                .orElseThrow(() -> new EcommerceBadRequestException(USER_NOT_FOUND))
                .toDomain();

        cashDomain.sub(request.getAmount());

        Cash cash = cashRepository.save(cashDomain.toEntity());

        CashHistory cashHistory = CashHistory.generateUseCashHistory(cash.getId(), request.getAmount());
        cashHistoryRepository.save(cashHistory);

        return cash.toDomain();
    }
}
