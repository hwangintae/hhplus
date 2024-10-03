package org.hhplus.speciallecture.common.exception;

import org.springframework.http.HttpStatus;

public class LectureTimeValidException extends SpecialLectureException {

    private static final String MESSAGE = "시간 설정이 잘못되었습니다.";

    public LectureTimeValidException() {
        super(MESSAGE);
    }

    @Override
    public int getCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
