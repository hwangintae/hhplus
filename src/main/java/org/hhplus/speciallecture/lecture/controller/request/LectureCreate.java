package org.hhplus.speciallecture.lecture.controller.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class LectureCreate {

    @NotNull(message = "코치 ID는 필수입니다.")
    private Long coachId;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
    private String title;

    @NotNull(message = "등록 시작 시간은 필수입니다.")
    private LocalDateTime registerStartDateTime;

    @NotNull(message = "등록 종료 시간은 필수입니다.")
    private LocalDateTime registerEndDateTime;

    @NotNull(message = "강의 시작 시간은 필수입니다.")
    private LocalDateTime lectureStartDateTime;

    @NotNull(message = "강의 종료 시간은 필수입니다.")
    private LocalDateTime lectureEndDateTime;

    @Min(value = 1, message = "학점은 최소 1이어야 합니다.")
    private int credit;

    @NotBlank(message = "강의실 이름은 필수입니다.")
    @Size(max = 50, message = "강의실 이름은 50자 이내여야 합니다.")
    private String roomName;

    private boolean online;

    @Min(value = 1, message = "최대 수용 인원은 최소 1명이어야 합니다.")
    @Max(value = 100, message = "최대 수용 인원은 100명을 초과할 수 없습니다.")
    private int maxCapacity;

    @Builder
    public LectureCreate(Long coachId, String title, LocalDateTime registerStartDateTime, LocalDateTime registerEndDateTime, LocalDateTime lectureStartDateTime, LocalDateTime lectureEndDateTime, int credit, String roomName, boolean online, int maxCapacity) {
        this.coachId = coachId;
        this.title = title;
        this.registerStartDateTime = registerStartDateTime;
        this.registerEndDateTime = registerEndDateTime;
        this.lectureStartDateTime = lectureStartDateTime;
        this.lectureEndDateTime = lectureEndDateTime;
        this.credit = credit;
        this.roomName = roomName;
        this.online = online;
        this.maxCapacity = maxCapacity;
    }

    public boolean validate(LocalDateTime now) {

        // registerStartDateTime                        수강 신청 시작일
        // registerEndDateTime                          수강 신청 종료일
        // lectureStartDateTime                         강의 시작 일
        // lectureEndDateTime                           강의 종료 일

        List<Boolean> validates = List.of(
                this.registerStartDateTime.isAfter(now),
                this.registerEndDateTime.isAfter(this.registerStartDateTime),
                this.lectureStartDateTime.isAfter(this.registerEndDateTime),
                this.lectureEndDateTime.isAfter(this.lectureStartDateTime)
        );

        List<Boolean> result = validates.stream().filter(b -> !b).toList();

        return result.isEmpty();
    }

    public LectureDomain toDomain() {
        return LectureDomain.builder()
                .coachId(this.coachId)
                .title(this.title)
                .registerStartDateTime(this.registerStartDateTime)
                .registerEndDateTime(this.registerEndDateTime)
                .lectureStartDateTime(this.lectureStartDateTime)
                .lectureEndDateTime(this.lectureEndDateTime)
                .credit(this.credit)
                .roomName(this.roomName)
                .online(this.online)
                .maxCapacity(this.maxCapacity)
                .build();
    }
}
