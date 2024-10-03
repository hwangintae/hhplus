package org.hhplus.speciallecture.lecture.service.lecture;

import lombok.RequiredArgsConstructor;
import org.hhplus.speciallecture.common.exception.LectureTimeOverlapsException;
import org.hhplus.speciallecture.lecture.domain.currentLectureCapacity.CurrentLectureCapacityDomain;
import org.hhplus.speciallecture.lecture.domain.currentLectureCapacity.CurrentLectureCapacityRepository;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureRepository;
import org.hhplus.speciallecture.lecture.domain.lectureEnrollment.LectureEnrollmentDomain;
import org.hhplus.speciallecture.lecture.domain.lectureEnrollment.LectureEnrollmentRepository;
import org.hhplus.speciallecture.lecture.service.lecture.response.LectureServiceResponse;
import org.hhplus.speciallecture.lecture.service.utill.LectureTimeChecker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;
    private final CurrentLectureCapacityRepository currentLectureCapacityRepository;
    private final LectureEnrollmentRepository lectureEnrollmentRepository;

    private final LectureTimeChecker lectureTimeChecker;

    @Transactional(rollbackFor = Exception.class)
    public LectureDomain save(LectureDomain lectureDomain) {
        List<LectureDomain> lectures = lectureRepository.getLectures(lectureDomain.getCoachId());

        List<LectureDomain> lectureDomains = lectures.stream()
                .filter(lecture -> lectureTimeChecker.isOverlaps(lecture, lectureDomain))
                .toList();

        if (!lectureDomains.isEmpty()) {
            throw new LectureTimeOverlapsException();
        }

        LectureDomain save = lectureRepository.save(lectureDomain);
        currentLectureCapacityRepository.save(save.getId(), 0);

        return save;
    }

    public List<LectureServiceResponse> getLectures(LocalDateTime now) {

        List<LectureDomain> lectureDomains = lectureRepository.getLectures(now);

        List<Long> lectureIds = lectureDomains.stream()
                .map(LectureDomain::getId)
                .toList();

        List<CurrentLectureCapacityDomain> capacityDomains = currentLectureCapacityRepository.getCurrentLectureCapacityDomains(lectureIds);

        Map<Long, LectureDomain> lectureMap = lectureDomains.stream()
                .collect(toMap(LectureDomain::getId, o -> o));

        Map<Long, Integer> capacityMap = capacityDomains.stream()
                .collect(toMap(CurrentLectureCapacityDomain::getLectureId, CurrentLectureCapacityDomain::getCurrentCapacity, (oldItem, newItem) -> newItem));

        return lectureMap.entrySet().stream()
                .map(entry -> {
                    Long id = entry.getKey();
                    LectureDomain lectureDomain = entry.getValue();

                    return LectureServiceResponse.of(lectureDomain,
                            capacityMap.getOrDefault(id, lectureDomain.getMaxCapacity())
                    );
                }).toList();
    }

    public List<LectureServiceResponse> getLectures(Long studentId) {
        List<LectureEnrollmentDomain> lectureEnrollmentDomains = lectureEnrollmentRepository.getLectureEnrollmentDomainByStudentId(studentId);

        List<Long> lectureIds = lectureEnrollmentDomains.stream()
                .map(LectureEnrollmentDomain::getLectureId)
                .toList();

        List<LectureDomain> lectures = lectureRepository.getLectures(lectureIds);

        return lectures.stream()
                .map(lecture -> LectureServiceResponse.create(lecture, -1)).toList();
    }

    public void changeCredit(Long lectureId, int credit) {
        LectureDomain lecture = lectureRepository.getLecture(lectureId);

        lecture.toEdit(lecture.toEditor()
                .credit(credit)
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public void enrollLecture(Long studentId, Long lectureId) {
        CurrentLectureCapacityDomain currentLectureCapacityDomain = currentLectureCapacityRepository
                .getCurrentLectureCapacityDomain(lectureId);

        lectureEnrollmentRepository.save(LectureEnrollmentDomain.builder()
                .studentId(studentId)
                .lectureId(lectureId)
                .deleteAt(false)
                .build());

        currentLectureCapacityDomain.add();
        currentLectureCapacityRepository.changeCurrentCapacity(currentLectureCapacityDomain);
    }
}
