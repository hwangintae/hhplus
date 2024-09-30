package io.hhplus.tdd.exception;

public class MinChargeException extends RuntimeException {

    private static final String MESSAGE = "1 이상의 포인트를 입력해 주세요.";

    public MinChargeException() {
        super(MESSAGE);
    }
}
