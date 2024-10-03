package org.hhplus.speciallecture.lecture.domain.lecture;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LectureDomain {

    private final Long id;
    private final Long coachId;
    private String title;
    private LocalDateTime registerStartDateTime;
    private LocalDateTime registerEndDateTime;
    private LocalDateTime lectureStartDateTime;
    private LocalDateTime lectureEndDateTime;
    private int credit;
    private String roomName;
    private boolean online;
    private int maxCapacity;

    @Builder
    protected LectureDomain(Long id, Long coachId, String title, LocalDateTime registerStartDateTime, LocalDateTime registerEndDateTime, LocalDateTime lectureStartDateTime, LocalDateTime lectureEndDateTime, int credit, String roomName, boolean online, int maxCapacity) {
        this.id = id;
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

    public LectureDomainEditor.LectureDomainEditorBuilder toEditor() {
        return LectureDomainEditor.builder()
                .title(this.title)
                .registerStartDateTime(this.registerStartDateTime)
                .registerEndDateTime(this.registerEndDateTime)
                .lectureStartDateTime(this.lectureStartDateTime)
                .lectureEndDateTime(this.lectureEndDateTime)
                .credit(this.credit)
                .roomName(this.roomName)
                .online(this.online)
                .maxCapacity(this.maxCapacity);
    }

    public void toEdit(LectureDomainEditor editor) {
        this.title = editor.getTitle();
        this.registerStartDateTime = editor.getRegisterStartDateTime();
        this.registerEndDateTime = editor.getRegisterEndDateTime();
        this.lectureStartDateTime = editor.getLectureStartDateTime();
        this.lectureEndDateTime = editor.getLectureEndDateTime();
        this.credit = editor.getCredit();
        this.roomName = editor.getRoomName();
        this.online = editor.isOnline();
        this.maxCapacity = editor.getMaxCapacity();
    }


}
