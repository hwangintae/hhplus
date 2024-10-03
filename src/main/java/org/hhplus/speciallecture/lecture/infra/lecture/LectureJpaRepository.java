package org.hhplus.speciallecture.lecture.infra.lecture;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LectureJpaRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByCoachId(Long coachId);

    List<Lecture> findByRegisterEndDateTimeAfter(LocalDateTime now);
    List<Lecture> findByIdIn(List<Long> lectureIds);
}
