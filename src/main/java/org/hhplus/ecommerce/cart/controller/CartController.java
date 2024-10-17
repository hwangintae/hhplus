package org.hhplus.ecommerce.cart.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hhplus.ecommerce.cart.service.*;
import org.hhplus.ecommerce.cart.usecase.CartUsecase;
import org.hhplus.ecommerce.common.RestApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart 관련 API입니다.")
public class CartController {

    private final CartUsecase cartUsecase;
    private final CartService cartService;

    @Operation(summary = "장바구니 조회", description = "장바구니에 저장된 상품을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "장바구니 조회 성공")
    @GetMapping("/cart")
    public RestApiResponse<List<CartItemResponse>> getCartItems(@RequestParam Long userId) {
        List<CartItemDomain> cartItems = cartService.getCartItems(userId);

        List<CartItemResponse> cartItemResponses = cartItems.stream().map(item -> {
                    return CartItemResponse.builder()
                            .itemId(item.getItemId())
                            .cnt(item.getCnt())
                            .deleteAt(item.isDeleteAt())
                            .build();
                })
                .toList();

        return RestApiResponse.ok(cartItemResponses);
    }

    @Operation(summary = "장바구니에 상품 담기", description = "장바구니에 상품을 추가합니다.")
    @ApiResponse(responseCode = "200", description = "장바구니에 상품 담기 성공")
    @PostMapping("/cart")
    public RestApiResponse<CartItemResponse> addCart(@RequestBody CartITemCreateWithUserId request) {
        CartItemDomain cartItemDomain = cartUsecase.addCart(request.getUserId(), request.getCartItemCreate());

        CartItemResponse cartItemResponse = CartItemResponse.builder()
                .itemId(cartItemDomain.getItemId())
                .cnt(cartItemDomain.getCnt())
                .deleteAt(cartItemDomain.isDeleteAt())
                .build();


        return RestApiResponse.ok(cartItemResponse);
    }

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에 저장된 상품을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @PutMapping("/cart")
    public RestApiResponse<String> deleteCartItem(@RequestBody CarItemDeleteRequest request) {

        // 시큐리티를 사용 했으면 userPrincipal과 비교
        Long userId = request.getUserId();

        cartService.deleteItem(request.getCartItemId());

        return RestApiResponse.ok("ok");
    }


}
