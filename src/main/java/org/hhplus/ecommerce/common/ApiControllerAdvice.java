package org.hhplus.ecommerce.common;

import lombok.extern.slf4j.Slf4j;
import org.hhplus.ecommerce.common.exception.EcommerceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(EcommerceException.class)
    public ApiResponse<String> handleEcommerceException(EcommerceException e) {

        log.error(">>> handleEcommerceException: {}", e.getMessage());

        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
