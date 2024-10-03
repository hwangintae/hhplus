package org.hhplus.speciallecture.lecture.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LectureEnrollmentRequest {
    private Long studentId;
    private Long lectureId;

    @Builder
    public LectureEnrollmentRequest(Long studentId, Long lectureId) {
        this.studentId = studentId;
        this.lectureId = lectureId;
    }
}
