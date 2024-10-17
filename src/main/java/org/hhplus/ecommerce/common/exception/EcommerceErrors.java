package org.hhplus.ecommerce.common.exception;

import lombok.Getter;

@Getter
public enum EcommerceErrors {

    USER_NOT_FOUND("존재하지 않는 사용자 입니다."),
    ITEM_NOT_FOUND("존재하지 않는 상품 입니다."),
    INSUFFICIENT_USER_CASH("잔액이 부족합니다."),
    INSUFFICIENT_STOCK("재고가 부족합니다."),
    INSUFFICIENT_STOCK_NOT_IN_CART("재고가 부족한 상품은 장바구니에 담을 수 없습니다."),
    ILLEGAL_AMOUNT("금액은 0보다 커야 합니다.")
    ;

    EcommerceErrors(String message) {
        this.message = message;
    }

    private final String message;
}
