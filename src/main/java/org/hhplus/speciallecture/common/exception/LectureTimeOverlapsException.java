package org.hhplus.speciallecture.common.exception;

import org.springframework.http.HttpStatus;

public class LectureTimeOverlapsException extends SpecialLectureException {

    private static final String MESSAGE = "동일한 시간대에 오직 하나의 강의만 진행할 수 있습니다.";

    public LectureTimeOverlapsException() {
        super(MESSAGE);
    }

    @Override
    public int getCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
