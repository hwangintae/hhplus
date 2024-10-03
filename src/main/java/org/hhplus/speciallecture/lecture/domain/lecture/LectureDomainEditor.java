package org.hhplus.speciallecture.lecture.domain.lecture;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LectureDomainEditor {
    private final String title;
    private final LocalDateTime registerStartDateTime;
    private final LocalDateTime registerEndDateTime;
    private final LocalDateTime lectureStartDateTime;
    private final LocalDateTime lectureEndDateTime;
    private final int credit;
    private final String roomName;
    private final boolean online;
    private final int maxCapacity;

    @Builder
    protected LectureDomainEditor(String title, LocalDateTime registerStartDateTime, LocalDateTime registerEndDateTime, LocalDateTime lectureStartDateTime, LocalDateTime lectureEndDateTime, int credit, String roomName, boolean online, int maxCapacity) {
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
}
