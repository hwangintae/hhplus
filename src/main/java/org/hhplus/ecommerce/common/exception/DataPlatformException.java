package org.hhplus.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

public class DataPlatformException extends EcommerceException {

    public DataPlatformException(EcommerceErrors errors) {
        super(errors.getMessage());
    }

    @Override
    public int getCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
