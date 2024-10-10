package org.hhplus.ecommerce.cash.controller;

import org.hhplus.ecommerce.cash.service.AddCashRequest;
import org.hhplus.ecommerce.cash.service.CashResponse;
import org.hhplus.ecommerce.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api")
public class CashController {

    @GetMapping("/cash")
    public ApiResponse<CashResponse> getCash(@RequestParam Long userId) {

        return ApiResponse.ok(CashResponse.builder()
                        .userId(userId)
                        .amount(new BigDecimal(1_000))
                .build());
    }

    @PostMapping("/cash")
    public ApiResponse<CashResponse> addCash(@RequestBody AddCashRequest request) {

        return ApiResponse.ok(CashResponse.builder()
                .userId(request.getUserId())
                .amount(new BigDecimal(1_000).add(request.getAmount()))
                .build());
    }
}
