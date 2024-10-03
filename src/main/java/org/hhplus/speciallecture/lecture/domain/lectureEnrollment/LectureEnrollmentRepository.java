package org.hhplus.speciallecture.lecture.domain.lectureEnrollment;

import java.util.List;

public interface LectureEnrollmentRepository {
    void save(LectureEnrollmentDomain lectureEnrollmentDomain);
    List<LectureEnrollmentDomain> getLectureEnrollmentDomainByStudentId(Long studentId);
    void deleteAllInBatch();
}
