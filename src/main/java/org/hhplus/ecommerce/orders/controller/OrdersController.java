package org.hhplus.ecommerce.orders.controller;

import org.hhplus.ecommerce.common.ApiResponse;
import org.hhplus.ecommerce.orders.service.OrderRequest;
import org.hhplus.ecommerce.orders.service.OrderRequests;
import org.hhplus.ecommerce.orders.service.OrderResponse;
import org.hhplus.ecommerce.orders.service.OrderStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrdersController {

    @PostMapping("/orders")
    public ApiResponse<String> orders(@RequestBody OrderRequests requests) {
        return ApiResponse.ok("상품 주문 성공");
    }

    @GetMapping("orders")
    public ApiResponse<List<OrderResponse>> orders(@RequestParam String userId) {

        List<OrderResponse> orderResponses = List.of(
                OrderResponse.builder()
                        .itemName("아이폰 16 프로 맥스")
                        .itemCnt(1)
                        .status(OrderStatus.ORDER)
                        .build(),
                OrderResponse.builder()
                        .itemName("아이폰 16 프로")
                        .itemCnt(10)
                        .status(OrderStatus.ORDER)
                        .build(),
                OrderResponse.builder()
                        .itemName("아이폰 16")
                        .itemCnt(11)
                        .status(OrderStatus.ORDER)
                        .build()
                );

        return ApiResponse.ok(orderResponses);
    }
}
