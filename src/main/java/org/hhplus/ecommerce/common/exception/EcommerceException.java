package org.hhplus.ecommerce.common.exception;

public abstract class EcommerceException extends RuntimeException {

    public EcommerceException(String message) {
        super(message);
    }

    public EcommerceException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getCode();
}
