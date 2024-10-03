package org.hhplus.speciallecture.lecture.domain.lecture;

import lombok.RequiredArgsConstructor;
import org.hhplus.speciallecture.lecture.infra.lecture.Lecture;
import org.hhplus.speciallecture.lecture.infra.lecture.LectureJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {

    private final LectureJpaRepository lectureJpaRepository;

    @Override
    public LectureDomain save(LectureDomain lectureDomain) {
        Lecture save = lectureJpaRepository.save(Lecture.of(lectureDomain));

        return save.toDomain();
    }

    public List<LectureDomain> getLectures(LocalDateTime now) {
        List<Lecture> lectures = lectureJpaRepository.findByRegisterEndDateTimeAfter(now);

        return lectures.stream()
                .map(Lecture::toDomain)
                .toList();
    }

    @Override
    public List<LectureDomain> getLectures(Long coachId) {
        List<Lecture> lectures = lectureJpaRepository.findByCoachId(coachId);

        return lectures.stream()
                .map(Lecture::toDomain)
                .toList();
    }

    @Override
    public List<LectureDomain> getLectures(List<Long> lectureIds) {
        List<Lecture> lectures = lectureJpaRepository.findByIdIn(lectureIds);

        return lectures.stream()
                .map(Lecture::toDomain)
                .toList();
    }

    @Override
    public LectureDomain getLecture(Long lectureId) {
        Lecture lecture = lectureJpaRepository.findById(lectureId)
                .orElseThrow(RuntimeException::new);
        return lecture.toDomain();
    }

    @Override
    public void deleteAllInBatch() {
        lectureJpaRepository.deleteAllInBatch();
    }

}
