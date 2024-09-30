package io.hhplus.tdd.exception;

public class MinusPointException extends RuntimeException {

    private static final String MESSAGE = "잔액이 모자랍니다.";

    public MinusPointException() {
        super(MESSAGE);
    }
}
