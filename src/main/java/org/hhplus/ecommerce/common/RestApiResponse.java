package org.hhplus.ecommerce.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public RestApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> RestApiResponse<T> of(int code, String message, T data) {
        return new RestApiResponse<>(code, message, data);
    }

    public static <T> RestApiResponse<T> ok(T data) {
        HttpStatus ok = HttpStatus.OK;

        return of(ok.value(), ok.name(), data);
    }

    public static RestApiResponse<String> error(int code, String message) {
        return of(code, message, "error");
    }
}
