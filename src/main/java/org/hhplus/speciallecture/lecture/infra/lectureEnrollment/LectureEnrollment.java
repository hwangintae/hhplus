package org.hhplus.speciallecture.lecture.infra.lectureEnrollment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.speciallecture.lecture.domain.lectureEnrollment.LectureEnrollmentDomain;
import org.hhplus.speciallecture.lecture.infra.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureEnrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lectureId;

    private Long studentId;

    private boolean deleteAt;

    @Builder
    protected LectureEnrollment(Long id, Long lectureId, Long studentId, boolean deleteAt) {
        this.id = id;
        this.lectureId = lectureId;
        this.studentId = studentId;
        this.deleteAt = deleteAt;
    }

    public static LectureEnrollment of(LectureEnrollmentDomain domain) {
        return LectureEnrollment.builder()
                .lectureId(domain.getLectureId())
                .studentId(domain.getStudentId())
                .deleteAt(domain.isDeleteAt())
                .build();
    }

    public LectureEnrollmentDomain toDomain() {
        return LectureEnrollmentDomain.builder()
                .id(this.id)
                .lectureId(this.lectureId)
                .studentId(this.studentId)
                .deleteAt(this.deleteAt)
                .build();
    }

    public void changeDelete(boolean deleteAt) {
        this.deleteAt = deleteAt;
    }
}
