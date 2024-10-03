package org.hhplus.speciallecture.lecture.infra.lecture;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;
import org.hhplus.speciallecture.lecture.infra.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long coachId;

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
    protected Lecture(Long coachId, String title, LocalDateTime registerStartDateTime, LocalDateTime registerEndDateTime, LocalDateTime lectureStartDateTime, LocalDateTime lectureEndDateTime, int credit, String roomName, boolean online, int maxCapacity) {
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

    public static Lecture of(LectureDomain domain) {
        return Lecture.builder()
                .coachId(domain.getCoachId())
                .title(domain.getTitle())
                .registerStartDateTime(domain.getRegisterStartDateTime())
                .registerEndDateTime(domain.getRegisterEndDateTime())
                .lectureStartDateTime(domain.getLectureStartDateTime())
                .lectureEndDateTime(domain.getLectureEndDateTime())
                .credit(domain.getCredit())
                .roomName(domain.getRoomName())
                .online(domain.isOnline())
                .maxCapacity(domain.getMaxCapacity())
                .build();
    }

    public LectureDomain toDomain() {
        return LectureDomain.builder()
                .id(this.id)
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
