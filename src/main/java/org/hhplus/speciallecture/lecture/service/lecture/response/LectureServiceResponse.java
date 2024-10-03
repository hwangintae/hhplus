package org.hhplus.speciallecture.lecture.service.lecture.response;

import lombok.Builder;
import lombok.Getter;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;

import java.time.LocalDateTime;

@Getter
public class LectureServiceResponse {

    private final Long lectureId;
    private final Long coachId;
    private final String title;
    private final LocalDateTime registerStartDateTime;
    private final LocalDateTime registerEndDateTime;
    private final LocalDateTime lectureStartDateTime;
    private final LocalDateTime lectureEndDateTime;
    private final int credit;
    private final String roomName;
    private final boolean online;
    private final int maxCapacity;
    private final String status;

    @Builder
    protected LectureServiceResponse(Long lectureId, Long coachId, String title, LocalDateTime registerStartDateTime, LocalDateTime registerEndDateTime, LocalDateTime lectureStartDateTime, LocalDateTime lectureEndDateTime, int credit, String roomName, boolean online, int maxCapacity, String status) {
        this.lectureId = lectureId;
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
        this.status = status;
    }

    public static LectureServiceResponse of(LectureDomain lectureDomain, int currentCapacity) {

        boolean status = lectureDomain.getMaxCapacity() > currentCapacity;

        int code = Boolean.compare(status, false);

        return create(lectureDomain, code);
    }

    public static LectureServiceResponse create(LectureDomain lectureDomain, int code) {
        LectureEnrollmentStatusEnum statusEnum = LectureEnrollmentStatusEnum.getStatusEnum(code);

        return LectureServiceResponse.builder()
                .lectureId(lectureDomain.getId())
                .coachId(lectureDomain.getCoachId())
                .title(lectureDomain.getTitle())
                .registerStartDateTime(lectureDomain.getRegisterStartDateTime())
                .registerEndDateTime(lectureDomain.getRegisterEndDateTime())
                .lectureStartDateTime(lectureDomain.getLectureStartDateTime())
                .lectureEndDateTime(lectureDomain.getLectureEndDateTime())
                .credit(lectureDomain.getCredit())
                .roomName(lectureDomain.getRoomName())
                .online(lectureDomain.isOnline())
                .maxCapacity(lectureDomain.getMaxCapacity())
                .status(statusEnum.getText())
                .build();
    }
}
