package io.hhplus.tdd.exception;

public class MaxChargeException extends RuntimeException {

    private static final String MESSAGE = "포인트는 최대 50,000,000(오천만)을 넘을 수 없습니다.";

    public MaxChargeException() {
        super(MESSAGE);
    }
}
