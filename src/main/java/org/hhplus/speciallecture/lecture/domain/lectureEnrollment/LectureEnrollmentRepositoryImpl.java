package org.hhplus.speciallecture.lecture.domain.lectureEnrollment;

import lombok.RequiredArgsConstructor;
import org.hhplus.speciallecture.lecture.infra.lectureEnrollment.LectureEnrollment;
import org.hhplus.speciallecture.lecture.infra.lectureEnrollment.LectureEnrollmentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureEnrollmentRepositoryImpl implements LectureEnrollmentRepository {

    private final LectureEnrollmentJpaRepository lectureEnrollmentJpaRepository;

    @Override
    public void save(LectureEnrollmentDomain lectureEnrollmentDomain) {
        lectureEnrollmentJpaRepository.save(LectureEnrollment.of(lectureEnrollmentDomain));
    }

    @Override
    public List<LectureEnrollmentDomain> getLectureEnrollmentDomainByStudentId(Long studentId) {
        List<LectureEnrollment> lectureEnrollments = lectureEnrollmentJpaRepository.findByStudentId(studentId);

        return lectureEnrollments.stream()
                .map(LectureEnrollment::toDomain)
                .toList();
    }

    @Override
    public void deleteAllInBatch() {
        lectureEnrollmentJpaRepository.deleteAllInBatch();
    }


}
