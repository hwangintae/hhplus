package org.hhplus.ecommerce.common.exception;

import lombok.Getter;

@Getter
public enum EcommerceErrors {

    USER_NOT_FOUND("존재하지 않는 사용자 입니다."),
    ITEM_NOT_FOUND("존재하지 않는 상품 입니다."),
    INSUFFICIENT_USER_CASH("잔액이 부족합니다."),
    INSUFFICIENT_STOCK("재고가 부족합니다.")
    ;

    EcommerceErrors(String message) {
        this.message = message;
    }

    private final String message;
}
