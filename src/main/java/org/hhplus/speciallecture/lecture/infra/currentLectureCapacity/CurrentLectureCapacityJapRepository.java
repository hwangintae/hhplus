package org.hhplus.speciallecture.lecture.infra.currentLectureCapacity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurrentLectureCapacityJapRepository extends JpaRepository<CurrentLectureCapacity, Long> {

    Optional<CurrentLectureCapacity> findByLectureId(Long lectureId);

    List<CurrentLectureCapacity> findByLectureIdIn(List<Long> lectureIds);
}
