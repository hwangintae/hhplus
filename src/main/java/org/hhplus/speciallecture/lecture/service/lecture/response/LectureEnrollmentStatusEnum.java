package org.hhplus.speciallecture.lecture.service.lecture.response;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum LectureEnrollmentStatusEnum {

    FULL("불가", 0),
    NOT_FULL("가능", 1),
    ENROLL("신청완료", -1),
    ERROR("오류", -1000);

    private final String text;
    private final int code;

    LectureEnrollmentStatusEnum(String text, int code) {
        this.text = text;
        this.code = code;
    }

    public static LectureEnrollmentStatusEnum getStatusEnum(int code) {
        return Stream.of(LectureEnrollmentStatusEnum.values())
                .filter(lectureEnrollmentStatusEnum -> lectureEnrollmentStatusEnum.code == code)
                .findFirst()
                .orElse(LectureEnrollmentStatusEnum.ERROR);
    }
}
