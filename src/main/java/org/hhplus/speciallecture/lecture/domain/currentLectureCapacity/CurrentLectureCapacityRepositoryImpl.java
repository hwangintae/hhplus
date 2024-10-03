package org.hhplus.speciallecture.lecture.domain.currentLectureCapacity;

import lombok.RequiredArgsConstructor;
import org.hhplus.speciallecture.common.exception.LectureNotFoundException;
import org.hhplus.speciallecture.lecture.infra.currentLectureCapacity.CurrentLectureCapacity;
import org.hhplus.speciallecture.lecture.infra.currentLectureCapacity.CurrentLectureCapacityJapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CurrentLectureCapacityRepositoryImpl implements CurrentLectureCapacityRepository {

    private final CurrentLectureCapacityJapRepository currentLectureCapacityJapRepository;

    @Override
    public CurrentLectureCapacityDomain getCurrentLectureCapacityDomain(Long lectureId) {
        CurrentLectureCapacity currentLectureCapacity = currentLectureCapacityJapRepository.findByLectureId(lectureId)
                .orElseThrow(LectureNotFoundException::new);

        return currentLectureCapacity.toDomain();
    }

    @Override
    public List<CurrentLectureCapacityDomain> getCurrentLectureCapacityDomains(List<Long> lectureIds) {
        List<CurrentLectureCapacity> currentLectureCapacities = currentLectureCapacityJapRepository.findByLectureIdIn(lectureIds);

        return currentLectureCapacities.stream()
                .map(CurrentLectureCapacity::toDomain)
                .toList();
    }

    @Override
    public void save(Long lectureId, int currentCapacity) {
        CurrentLectureCapacityDomain currentLectureCapacityDomain = CurrentLectureCapacityDomain.builder()
                .lectureId(lectureId)
                .currentCapacity(currentCapacity)
                .build();

        currentLectureCapacityJapRepository.save(CurrentLectureCapacity.of(currentLectureCapacityDomain));
    }

    @Override
    public void changeCurrentCapacity(CurrentLectureCapacityDomain domain) {
        CurrentLectureCapacity currentLectureCapacity = currentLectureCapacityJapRepository.findById(domain.getId())
                .orElseThrow(LectureNotFoundException::new);

        currentLectureCapacity.changeCapacity(domain.getCurrentCapacity());
    }

    @Override
    public void deleteAllInBatch() {
        currentLectureCapacityJapRepository.deleteAllInBatch();
    }
}
