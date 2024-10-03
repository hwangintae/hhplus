package org.hhplus.speciallecture.lecture.service;

import org.assertj.core.api.Assertions;
import org.hhplus.speciallecture.common.exception.DuplicatedEnrollmentException;
import org.hhplus.speciallecture.common.exception.LectureEnrollmentException;
import org.hhplus.speciallecture.common.exception.LectureTimeOverlapsException;
import org.hhplus.speciallecture.lecture.controller.LectureController;
import org.hhplus.speciallecture.lecture.domain.currentLectureCapacity.CurrentLectureCapacityDomain;
import org.hhplus.speciallecture.lecture.domain.currentLectureCapacity.CurrentLectureCapacityRepository;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureRepository;
import org.hhplus.speciallecture.lecture.domain.lectureEnrollment.LectureEnrollmentRepository;
import org.hhplus.speciallecture.lecture.service.lecture.LectureService;
import org.hhplus.speciallecture.lecture.service.lecture.response.LectureServiceResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest
class LectureServiceIntegrationTest {

    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private CurrentLectureCapacityRepository currentLectureCapacityRepository;

    @Autowired
    private LectureEnrollmentRepository lectureEnrollmentRepository;
    @Autowired
    private LectureController lecture;

    @AfterEach
    void tearDown() {
        lectureEnrollmentRepository.deleteAllInBatch();
        currentLectureCapacityRepository.deleteAllInBatch();
        lectureRepository.deleteAllInBatch();
    }


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

        lectureDomains.forEach(lectureDomain -> {
            lectureService.save(lectureDomain);
        });


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
        long lastIdx = 4L;

        List<LectureDomain> lectureDomains = LongStream.range(0, lastIdx)
                .mapToObj(idx -> LectureDomain.builder()
                        .coachId(coachId)
                        .title("test_" + idx)
                        .lectureStartDateTime(now.plusHours(idx))
                        .lectureEndDateTime(now.plusHours(idx + 1))
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

        lectureDomains.forEach(lectureDomain -> lectureService.save(lectureDomain));

        LectureDomain testLectureDomain = LectureDomain.builder()
                .coachId(coachId)
                .title("test_" + lastIdx)
                .lectureStartDateTime(now.plusHours(lastIdx))
                .lectureEndDateTime(now.plusHours(lastIdx + 1))
                .build();


        // when
        lectureService.save(testLectureDomain);

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
    @DisplayName("강의는 최대 30명 까지 수용할 수 있으며, 30명 초과시 LectureEnrollmentException 가 발생한다.")
    public void overLectureEnrollment() {
        // given
        Long studentId = 31L;

        LectureDomain lectureDomain = LectureDomain.builder()
                .maxCapacity(30)
                .build();

        LectureDomain save = lectureService.save(lectureDomain);

        Long lectureId = save.getId();

        LongStream.rangeClosed(1, 30)
                .forEach(idx -> lectureService.enrollLecture(idx, lectureId));

        // when // then
        assertThatThrownBy(() -> lectureService.enrollLecture(studentId, lectureId))
                .isInstanceOf(LectureEnrollmentException.class)
                .hasMessage("해당 강좌는 정원이 초과되어 더 이상 신청할 수 없습니다.");
    }

    @Test
    @DisplayName("수강 신청 기간인 강의의 목록을 출력한다.")
    public void getLectures() {
        // given
        LocalDateTime now = LocalDateTime.now();

        List<LectureDomain> lectureDomains = List.of(
                LectureDomain.builder().coachId(1L).title("test1").registerStartDateTime(now).registerEndDateTime(now.plusDays(2)).lectureStartDateTime(now.plusDays(3)).lectureEndDateTime(now.plusDays(3).plusHours(1)).credit(300).roomName("1706호").online(false).maxCapacity(30).build(),
                LectureDomain.builder().coachId(2L).title("test2").registerStartDateTime(now.minusDays(2)).registerEndDateTime(now.minusDays(1)).lectureStartDateTime(now.plusDays(4)).lectureEndDateTime(now.plusDays(4).plusHours(1)).credit(300).roomName("1706호").online(false).maxCapacity(30).build(),
                LectureDomain.builder().coachId(3L).title("test2").registerStartDateTime(now.minusDays(2)).registerEndDateTime(now.minusDays(1)).lectureStartDateTime(now.plusDays(5)).lectureEndDateTime(now.plusDays(5).plusHours(1)).credit(300).roomName("1706호").online(false).maxCapacity(30).build(),
                LectureDomain.builder().coachId(4L).title("test2").registerStartDateTime(now.minusDays(2)).registerEndDateTime(now.minusDays(1)).lectureStartDateTime(now.plusDays(6)).lectureEndDateTime(now.plusDays(6).plusHours(1)).credit(300).roomName("1706호").online(false).maxCapacity(30).build(),
                LectureDomain.builder().coachId(5L).title("test6").registerStartDateTime(now.plusDays(3)).registerEndDateTime(now.plusDays(7)).lectureStartDateTime(now.plusDays(8)).lectureEndDateTime(now.plusDays(8).plusHours(1)).credit(300).roomName("1706호").online(false).maxCapacity(30).build()
        );

        lectureDomains.forEach(lectureDomain -> lectureService.save(lectureDomain));

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


    // 동시성 통합 테스트
    @Test
    @DisplayName("동시에 30명이 강의를 신청했을 시, 30명이 등록된다.")
    public void concurrency30() {
        // given
        LectureDomain lectureDomain = LectureDomain.builder()
                .maxCapacity(30)
                .build();

        LectureDomain save = lectureService.save(lectureDomain);

        Long lectureId = save.getId();

        // when
        CompletableFuture.allOf(
                LongStream.rangeClosed(1, 30)
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> lectureService.enrollLecture(idx, lectureId)))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        CurrentLectureCapacityDomain capacityDomain = lectureService.getCurrnentLectureCapacity(lectureId);
        Assertions.assertThat(capacityDomain.getCurrentCapacity()).isEqualTo(30);
    }

    @Test
    @DisplayName("동시에 40명이 강의를 신청했을 시, 30명은 등록되고 31번째 부터 LectureEnrollmentException 이 발생한다.")
    public void concurrency40() {
        // given
        LectureDomain lectureDomain = LectureDomain.builder()
                .maxCapacity(30)
                .build();

        LectureDomain save = lectureService.save(lectureDomain);

        Long lectureId = save.getId();

        // when
        AtomicInteger exceptionCount = new AtomicInteger();
        CompletableFuture.allOf(
                LongStream.rangeClosed(1, 40)
                        .mapToObj(idx -> CompletableFuture.runAsync(() -> {
                            try {
                                lectureService.enrollLecture(idx, lectureId);
                            } catch (LectureEnrollmentException e) {
                                exceptionCount.getAndIncrement();
                            }
                        }))
                        .toArray(CompletableFuture[]::new)
        ).join();

        // then
        CurrentLectureCapacityDomain capacityDomain = lectureService.getCurrnentLectureCapacity(lectureId);
        assertThat(capacityDomain.getCurrentCapacity()).isEqualTo(30);
        assertThat(exceptionCount.get()).isEqualTo(10);
    }

}