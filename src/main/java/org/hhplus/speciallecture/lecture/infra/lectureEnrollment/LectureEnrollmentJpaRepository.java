package org.hhplus.speciallecture.lecture.infra.lectureEnrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureEnrollmentJpaRepository extends JpaRepository<LectureEnrollment, Long> {
    List<LectureEnrollment> findByStudentId(Long studentId);
}
