package org.hhplus.speciallecture.common.exception;

import org.springframework.http.HttpStatus;

public class LectureNotFoundException extends SpecialLectureException {

    private static final String MESSAGE = "유효하지 않은 강의 입니다.";

    public LectureNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
