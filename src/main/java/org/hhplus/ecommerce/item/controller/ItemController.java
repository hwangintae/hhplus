package org.hhplus.ecommerce.item.controller;

import org.hhplus.ecommerce.common.ApiResponse;
import org.hhplus.ecommerce.item.usecase.ItemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemController {

    @GetMapping("/item")
    public ApiResponse<ItemResponse> getItem(@RequestParam Long itemId) {
        return ApiResponse.ok(ItemResponse.builder()
                .name("아이폰 16 프로 맥스")
                .price(1_900_000L)
                .quantity(100)
                .build());
    }

    @GetMapping("items")
    public ApiResponse<List<ItemResponse>> getItems() {
        List<ItemResponse> itemResponses = List.of(
                ItemResponse.builder()
                        .name("아이폰 16 프로 맥스")
                        .price(1_900_000L)
                        .quantity(100)
                        .build(),
                ItemResponse.builder()
                        .name("아이폰 16 프로")
                        .price(1_550_000L)
                        .quantity(101)
                        .build(),
                ItemResponse.builder()
                        .name("아이폰 16 플러스")
                        .price(1_350_000L)
                        .quantity(110)
                        .build(),
                ItemResponse.builder()
                        .name("아이폰 16")
                        .price(1_250_000L)
                        .quantity(111)
                        .build()
                );

        return ApiResponse.ok(itemResponses);
    }
}
