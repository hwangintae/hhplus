package org.hhplus.ecommerce.cart.controller;

import org.hhplus.ecommerce.cart.service.CartItemStatus;
import org.hhplus.ecommerce.cart.service.CartResponse;
import org.hhplus.ecommerce.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @GetMapping("/cart")
    public ApiResponse<List<CartResponse>> getCart(@RequestParam Long userId) {
        List<CartResponse> cartResponses = List.of(
                CartResponse.builder()
                        .itemName("아이폰 16 프로 맥스")
                        .itemCnt(1)
                        .status(CartItemStatus.SALE)
                        .build(),
                CartResponse.builder()
                        .itemName("아이폰 16 프로")
                        .itemCnt(10)
                        .status(CartItemStatus.SALE)
                        .build(),
                CartResponse.builder()
                        .itemName("아이폰 16")
                        .itemCnt(11)
                        .status(CartItemStatus.SOLD_OUT)
                        .build()
        );

        return ApiResponse.ok(cartResponses);
    }
}
