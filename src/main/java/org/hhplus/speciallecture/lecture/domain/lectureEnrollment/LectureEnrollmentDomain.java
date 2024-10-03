package org.hhplus.speciallecture.lecture.domain.lectureEnrollment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureEnrollmentDomain {

    private final Long id;
    private final Long lectureId;
    private final Long studentId;
    private boolean deleteAt;

    @Builder
    protected LectureEnrollmentDomain(Long id, Long lectureId, Long studentId, boolean deleteAt) {
        this.id = id;
        this.lectureId = lectureId;
        this.studentId = studentId;
        this.deleteAt = deleteAt;
    }

    public void changeDelete(boolean delete) {
        this.deleteAt = delete;
    }
}
