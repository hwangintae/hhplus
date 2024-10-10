package org.hhplus.ecommerce.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        HttpStatus ok = HttpStatus.OK;

        return of(ok.value(), ok.name(), data);
    }

    public static ApiResponse<String> error(int code, String message) {
        return of(code, message, "error");
    }
}
