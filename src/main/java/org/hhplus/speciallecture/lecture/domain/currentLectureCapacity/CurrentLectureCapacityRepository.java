package org.hhplus.speciallecture.lecture.domain.currentLectureCapacity;

import java.util.List;

public interface CurrentLectureCapacityRepository {
    CurrentLectureCapacityDomain getCurrentLectureCapacityDomain(Long lectureId);
    List<CurrentLectureCapacityDomain> getCurrentLectureCapacityDomains(List<Long> lectureIds);
    void save(Long lectureId, int currentCapacity);
    void changeCurrentCapacity(CurrentLectureCapacityDomain currentLectureCapacityDomain);
    void deleteAllInBatch();
}
