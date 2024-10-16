package org.hhplus.ecommerce.orders.controller;

import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.ApiResponse;
import org.hhplus.ecommerce.dataPlatform.DataPlatformService;
import org.hhplus.ecommerce.orders.service.OrderItemDomain;
import org.hhplus.ecommerce.orders.service.OrderRequestsWithUserId;
import org.hhplus.ecommerce.orders.usecase.OrderResponse;
import org.hhplus.ecommerce.orders.usecase.OrderStatus;
import org.hhplus.ecommerce.orders.usecase.OrdersFacade;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersFacade ordersFacade;
    private final DataPlatformService dataPlatformService;

    @PostMapping("/orders")
    public ApiResponse<List<OrderItemDomain>> orders(@RequestBody OrderRequestsWithUserId request) {

        List<OrderItemDomain> orderItemDomains =
                ordersFacade.createOrder(request.getUserId(), request.getOrderRequests());

        dataPlatformService.sendData(orderItemDomains);

        return ApiResponse.ok(orderItemDomains);
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> orders(@RequestParam String userId) {

        List<OrderResponse> orderResponses = List.of(
                OrderResponse.builder()
                        .itemName("아이폰 16 프로 맥스")
                        .itemCnt(1)
                        .status(OrderStatus.SUCCESS)
                        .build(),
                OrderResponse.builder()
                        .itemName("아이폰 16 프로")
                        .itemCnt(10)
                        .status(OrderStatus.SUCCESS)
                        .build(),
                OrderResponse.builder()
                        .itemName("아이폰 16")
                        .itemCnt(11)
                        .status(OrderStatus.SUCCESS)
                        .build()
                );

        return ApiResponse.ok(orderResponses);
    }
}
