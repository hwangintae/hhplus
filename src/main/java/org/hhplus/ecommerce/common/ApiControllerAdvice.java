package org.hhplus.ecommerce.common;

import lombok.extern.slf4j.Slf4j;
import org.hhplus.ecommerce.common.exception.EcommerceException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public RestApiResponse<String> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {

        log.warn(">>> EmptyResultDataAccessException : {}", e.getMessage());

        return RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public RestApiResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {

        log.warn(">>> IllegalArgumentException : {}", e.getMessage());

        return RestApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
