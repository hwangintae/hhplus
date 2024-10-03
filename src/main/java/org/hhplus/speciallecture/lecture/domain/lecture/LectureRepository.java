package org.hhplus.speciallecture.lecture.domain.lecture;

import java.time.LocalDateTime;
import java.util.List;

public interface LectureRepository {
    LectureDomain save(LectureDomain lectureDomain);
    List<LectureDomain> getLectures(LocalDateTime now);
    List<LectureDomain> getLectures(Long coachId);
    List<LectureDomain> getLectures(List<Long> lectureIds);
    LectureDomain getLecture(Long lectureId);
    void deleteAllInBatch();
}
