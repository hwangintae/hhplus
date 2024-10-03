package org.hhplus.speciallecture.lecture.service;

import org.hhplus.speciallecture.common.exception.LectureEnrollmentException;
import org.hhplus.speciallecture.common.exception.LectureTimeOverlapsException;
import org.hhplus.speciallecture.lecture.domain.currentLectureCapacity.CurrentLectureCapacityDomain;
import org.hhplus.speciallecture.lecture.domain.currentLectureCapacity.CurrentLectureCapacityRepository;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureRepository;
import org.hhplus.speciallecture.lecture.domain.lectureEnrollment.LectureEnrollmentRepository;
import org.hhplus.speciallecture.lecture.service.lecture.LectureService;
import org.hhplus.speciallecture.lecture.service.lecture.response.LectureServiceResponse;
import org.hhplus.speciallecture.lecture.service.utill.LectureTimeChecker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {

    @InjectMocks
    private LectureService lectureService;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private CurrentLectureCapacityRepository currentLectureCapacityRepository;

    @Mock
    private LectureEnrollmentRepository lectureEnrollmentRepository;


    @Mock
    private LectureTimeChecker lectureTimeChecker;

    @Test
    @DisplayName("코치는 동일한 시간대에 오직 하나의 강의만 진행할 수 있다. 중복되는 시간대가 발생하면 LectureTimeOverlapsException 이 발생한다.")
    public void OverlapTest() {
        // given
        Long coachId = 11231L;

        LocalDateTime now = LocalDateTime.now();

        List<LectureDomain> lectureDomains = LongStream.rangeClosed(0, 3)
                .mapToObj(idx -> LectureDomain.builder()
                        .coachId(coachId)
                        .title("test_" + idx)
                        .lectureStartDateTime(now.plusHours(idx))
                        .lectureEndDateTime(now.plusHours(idx + 1))
                        .build())
                .toList();

        given(lectureRepository.getLectures(anyLong())).willReturn(lectureDomains);
        given(lectureTimeChecker.isOverlaps(any(LectureDomain.class), any(LectureDomain.class))).willCallRealMethod();

        // when // then
        LectureDomain testLectureDomain = LectureDomain.builder()
                .coachId(coachId)
                .title("test")
                .lectureStartDateTime(now.plusHours(1))
                .lectureEndDateTime(now.plusHours(2))
                .build();

        assertThatThrownBy(() -> lectureService.save(testLectureDomain))
                .isInstanceOf(LectureTimeOverlapsException.class)
                .hasMessage("동일한 시간대에 오직 하나의 강의만 진행할 수 있습니다.");

    }

    @Test
    @DisplayName("강의 시간이 겹치지 않으면 강의를 개설할 수 있다.")
    public void notOverlapTest() {
        // given
        Long coachId = 131L;

        LocalDateTime now = LocalDateTime.now();
        Long lastIdx = 4L;

        List<LectureDomain> lectureDomains = LongStream.range(0, lastIdx)
                .mapToObj(idx -> LectureDomain.builder()
                        .coachId(coachId)
                        .title("test_" + idx)
                        .lectureStartDateTime(now.plusHours(idx))
                        .lectureEndDateTime(now.plusHours(idx + 1))
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        given(lectureRepository.getLectures(anyLong())).willReturn(lectureDomains);
        given(lectureTimeChecker.isOverlaps(any(LectureDomain.class), any(LectureDomain.class))).willCallRealMethod();

        LectureDomain testLectureDomain = LectureDomain.builder()
                .coachId(coachId)
                .title("test_" + lastIdx)
                .lectureStartDateTime(now.plusHours(lastIdx))
                .lectureEndDateTime(now.plusHours(lastIdx + 1))
                .build();

        given(lectureRepository.save(any(LectureDomain.class))).willReturn(testLectureDomain);
        doNothing().when(currentLectureCapacityRepository).save(nullable(Long.class), any(int.class));

        // when
        lectureService.save(testLectureDomain);

        lectureDomains.add(testLectureDomain);
        when(lectureRepository.getLectures(anyLong())).thenReturn(lectureDomains);

        //then
        assertThat(lectureRepository.getLectures(coachId))
                .extracting("coachId", "title", "lectureStartDateTime", "lectureEndDateTime")
                .containsAnyOf(
                        tuple(coachId, "test_0", now.plusHours(0), now.plusHours(1)),
                        tuple(coachId, "test_1", now.plusHours(1), now.plusHours(2)),
                        tuple(coachId, "test_2", now.plusHours(2), now.plusHours(3)),
                        tuple(coachId, "test_3", now.plusHours(3), now.plusHours(4)),
                        tuple(coachId, "test_4", now.plusHours(4), now.plusHours(5))
                );
    }


    @Test
    @DisplayName("수강 신청 기간인 강의의 목록을 출력한다.")
    public void getLectures() {
        // given
        LocalDateTime now = LocalDateTime.now();

        List<LectureDomain> lectureDomains = List.of(
                LectureDomain.builder().id(1L).coachId(1L).title("test1").registerStartDateTime(now).registerEndDateTime(now.plusDays(2)).lectureStartDateTime(now.plusDays(3)).lectureEndDateTime(now.plusDays(3).plusHours(1)).credit(300).roomName("1706호").online(false).maxCapacity(30).build(),
                LectureDomain.builder().id(2L).coachId(5L).title("test6").registerStartDateTime(now.plusDays(3)).registerEndDateTime(now.plusDays(7)).lectureStartDateTime(now.plusDays(8)).lectureEndDateTime(now.plusDays(8).plusHours(1)).credit(300).roomName("1706호").online(false).maxCapacity(30).build()
        );

        List<CurrentLectureCapacityDomain> currentLectureCapacityDomains = List.of(
                CurrentLectureCapacityDomain.builder().id(1L).lectureId(1L).currentCapacity(0).build(),
                CurrentLectureCapacityDomain.builder().id(2L).lectureId(2L).currentCapacity(0).build()
        );

        given(lectureRepository.getLectures(any(LocalDateTime.class))).willReturn(lectureDomains);
        given(currentLectureCapacityRepository.getCurrentLectureCapacityDomains(List.of(1L, 2L))).willReturn(currentLectureCapacityDomains);

        // when
        List<LectureServiceResponse> lectures = lectureService.getLectures(now);

        // then
        assertThat(lectures)
                .extracting("coachId", "title", "registerStartDateTime", "registerEndDateTime", "lectureStartDateTime", "lectureEndDateTime", "credit", "roomName", "online", "maxCapacity", "status")
                .containsAnyOf(
                        tuple(1L, "test1", now, now.plusDays(2), now.plusDays(3), now.plusDays(3).plusHours(1), 300, "1706호", false, 30, "가능"),
                        tuple(5L, "test6", now.plusDays(3), now.plusDays(7), now.plusDays(8), now.plusDays(8).plusHours(1), 300, "1706호", false, 30, "가능")
                );
    }

}