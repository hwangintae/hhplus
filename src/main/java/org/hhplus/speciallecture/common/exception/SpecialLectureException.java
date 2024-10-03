package org.hhplus.speciallecture.common.exception;

public abstract class SpecialLectureException extends RuntimeException {
    public SpecialLectureException(String message) {
        super(message);
    }

    public SpecialLectureException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getCode();
}
