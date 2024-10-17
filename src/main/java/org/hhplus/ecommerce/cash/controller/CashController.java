package org.hhplus.ecommerce.cash.controller;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @GetMapping("/cash")
    public ApiResponse<CashResponse> getCash(@RequestParam Long userId) {

        CashDomain cashDomain = cashService.getCash(userId);

        return ApiResponse.ok(CashResponse.of(cashDomain));
    }

    @PostMapping("/cash")
    public ApiResponse<CashResponse> addCash(@RequestBody CashRequest request) {

        CashDomain cashDomain = cashService.addCash(request);

        return ApiResponse.ok(CashResponse.of(cashDomain));
    }
}
