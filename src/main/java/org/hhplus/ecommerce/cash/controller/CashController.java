package org.hhplus.ecommerce.cash.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cash.service.CashRequest;
import org.hhplus.ecommerce.cash.service.CashDomain;
import org.hhplus.ecommerce.cash.service.CashService;
import org.hhplus.ecommerce.common.RestApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Cash", description = "Cash 관련 API입니다.")
public class CashController {

    private final CashService cashService;

    @Operation(summary = "잔액 조회", description = "사용자의 잔액을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "잔액 조회 성공")
    @GetMapping("/cash")
    public RestApiResponse<CashResponse> getCash(@RequestParam Long userId) {

        CashDomain cashDomain = cashService.getCash(userId);

        return RestApiResponse.ok(CashResponse.of(cashDomain));
    }

    @Operation(summary = "잔액 충전", description = "사용자의 잔액을 충전합니다.")
    @ApiResponse(responseCode = "200", description = "잔액 충전 성공")
    @PostMapping("/cash")
    public RestApiResponse<CashResponse> addCash(@RequestBody CashRequest request) {

        CashDomain cashDomain = cashService.addCash(request);

        return RestApiResponse.ok(CashResponse.of(cashDomain));
    }
}
