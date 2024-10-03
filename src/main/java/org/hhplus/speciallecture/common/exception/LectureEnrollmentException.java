package org.hhplus.speciallecture.common.exception;

import org.springframework.http.HttpStatus;

public class LectureEnrollmentException extends SpecialLectureException {

    private static final String MESSAGE = "해당 강좌는 정원이 초과되어 더 이상 신청할 수 없습니다.";

    public LectureEnrollmentException() {
        super(MESSAGE);
    }

    @Override
    public int getCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
