package org.hhplus.ecommerce.item.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.common.RestApiResponse;
import org.hhplus.ecommerce.item.usecase.ItemFacade;
import org.hhplus.ecommerce.item.usecase.ItemResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Item", description = "Item 관련 API입니다.")
public class ItemController {

    private final ItemFacade itemFacade;

    @Operation(summary = "상품 조회", description = "상품을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    @GetMapping("/item")
    public RestApiResponse<ItemResponse> getItem(@RequestParam Long itemId) {

        return RestApiResponse.ok(itemFacade.getItemWithStock(itemId));
    }

    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공")
    @GetMapping("items")
    public RestApiResponse<List<ItemResponse>> getItems(List<Long> itemIds) {
        List<ItemResponse> itemResponses = itemFacade.getItemsWithStock(itemIds);

        return RestApiResponse.ok(itemResponses);
    }
}
