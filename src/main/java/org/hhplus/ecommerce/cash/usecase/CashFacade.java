package org.hhplus.ecommerce.cash.usecase;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.common.DistributedLock;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CashFacade {

    private final CashService cashService;
    private final DistributedLock distributedLock;

    public CashDomain addCash(CashRequest request) {
        return distributedLock.redissonLock("cash:" + request.getUserId(), () -> cashService.addCash(request));
    }

}
