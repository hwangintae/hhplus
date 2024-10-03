package org.hhplus.speciallecture.lecture.infra.currentLectureCapacity;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

public interface CurrentLectureCapacityJapRepository extends JpaRepository<CurrentLectureCapacity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "5000")})
    Optional<CurrentLectureCapacity> findByLectureId(Long lectureId);

    List<CurrentLectureCapacity> findByLectureIdIn(List<Long> lectureIds);
}
