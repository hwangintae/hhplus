package org.hhplus.speciallecture.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicatedEnrollmentException extends SpecialLectureException {

    private static final String MESSAGE = "이미 신청한 강의 입니다.";

    public DuplicatedEnrollmentException() {
        super(MESSAGE);
    }

    @Override
    public int getCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
