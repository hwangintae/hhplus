package org.hhplus.ecommerce.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.RestApiResponse;
import org.hhplus.ecommerce.dataPlatform.DataPlatformService;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequestsWithUserId;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.hhplus.ecommerce.orders.usecase.OrdersFacade;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order 관련 API입니다.")
public class OrdersController {

    private final OrdersFacade ordersFacade;
    private final OrdersService ordersService;

    @Operation(summary = "상품 주문", description = "상품을 주문 합니다.")
    @ApiResponse(responseCode = "200", description = "상품 주문 성공")
    @PostMapping("/orders")
    public RestApiResponse<List<OrderResponse>> orders(@RequestBody OrderRequestsWithUserId request) {

        List<OrderItemDomain> orderItemDomains =
                ordersFacade.createOrder(request.getUserId(), request.getOrderRequests());

        List<OrderResponse> orderResponses = orderItemDomains.stream()
                .map(item -> OrderResponse.builder()
                        .ordersId(item.getOrdersId())
                        .itemId(item.getItemId())
                        .itemCnt(item.getItemCnt())
                        .build())
                .toList();

        return RestApiResponse.ok(orderResponses);
    }

    @Operation(summary = "상품 주문 목록", description = "상품 주문 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상품 주문 목록 조회 성공")
    @GetMapping("/orders")
    public RestApiResponse<List<OrderResponse>> orders(@RequestParam Long userId) {

        List<OrderItemDomain> orderItemDomains = ordersService.getOrders(userId);

        List<OrderResponse> orderResponses = orderItemDomains.stream()
                .map(item -> OrderResponse.builder()
                        .ordersId(item.getOrdersId())
                        .itemId(item.getItemId())
                        .itemCnt(item.getItemCnt())
                        .build())
                .toList();

        return RestApiResponse.ok(orderResponses);
    }
}
