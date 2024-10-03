package org.hhplus.speciallecture.lecture.infra.currentLectureCapacity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hhplus.speciallecture.lecture.domain.currentLectureCapacity.CurrentLectureCapacityDomain;
import org.hhplus.speciallecture.lecture.infra.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentLectureCapacity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false, unique = true)
    private Long lectureId;

    private int currentCapacity;

    @Builder
    protected CurrentLectureCapacity(Long id, Long lectureId, int currentCapacity) {
        this.id = id;
        this.lectureId = lectureId;
        this.currentCapacity = currentCapacity;
    }

    public static CurrentLectureCapacity of(CurrentLectureCapacityDomain domain) {
        return CurrentLectureCapacity.builder()
                .lectureId(domain.getLectureId())
                .currentCapacity(domain.getCurrentCapacity())
                .build();
    }

    public CurrentLectureCapacityDomain toDomain() {
        return CurrentLectureCapacityDomain.builder()
                .id(this.id)
                .lectureId(this.lectureId)
                .currentCapacity(this.currentCapacity)
                .build();
    }

    public void changeCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }
}
