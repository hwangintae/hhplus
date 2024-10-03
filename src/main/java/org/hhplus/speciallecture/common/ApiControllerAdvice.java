package org.hhplus.speciallecture.common;

import org.hhplus.speciallecture.common.exception.SpecialLectureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RestControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(SpecialLectureException.class)
    public ApiResponse<String> handleSpecialLectureException(SpecialLectureException e) {
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> invalidRequestHandler(MethodArgumentNotValidException e) {

        Map<String, String> fieldErrors = e.getFieldErrors().stream()
                .collect(toMap(FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null ? "" : fieldError.getDefaultMessage()));

        return ApiResponse.error(fieldErrors);

    }
}
