package org.hhplus.speciallecture.lecture.infra.lectureEnrollment;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

public interface LectureEnrollmentJpaRepository extends JpaRepository<LectureEnrollment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    List<LectureEnrollment> findByStudentId(Long studentId);
}
