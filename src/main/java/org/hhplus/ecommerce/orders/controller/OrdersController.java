package org.hhplus.ecommerce.orders.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.RestApiResponse;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequestsWithUserId;
import org.hhplus.ecommerce.orders.service.OrdersService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Order 관련 API입니다.")
public class OrdersController {

    private final OrdersService ordersService;

    @Operation(summary = "상품 주문 요청", description = "상품을 주문 요청 합니다.")
    @ApiResponse(responseCode = "200", description = "상품 주문 요청 성공")
    @PostMapping("/orders")
    public RestApiResponse<List<OrderResponse>> orders(@RequestBody OrderRequestsWithUserId request) {

        List<OrderItemDomain> orderItemDomains = ordersService.createOrders(request.getUserId(), request.getOrderRequests());

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

    @GetMapping("/orders/popular")
    public Object getPopularItems(@RequestParam int from, @RequestParam int limit) {
        return ordersService.getPopularItems(from, limit);
    }

    @GetMapping("/orders/popular/clear")
    public void clearPopularItems() {
        ordersService.clearPopularItemsCache();
    }

    @GetMapping("/orders/status")
    public RestApiResponse<List<OrderResponse>> ordersStatus(@RequestParam Long ordersId) {

        List<OrderItemDomain> orderItemDomains = ordersService.getOrderItemsByOrderId(ordersId);

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
