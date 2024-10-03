package org.hhplus.speciallecture.lecture.domain.currentLectureCapacity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CurrentLectureCapacityDomain {

    private final Long id;
    private final Long lectureId;
    private int currentCapacity;

    @Builder
    protected CurrentLectureCapacityDomain(Long id, Long lectureId, int currentCapacity) {
        this.id = id;
        this.lectureId = lectureId;
        this.currentCapacity = currentCapacity;
    }

    public void add() {
        this.currentCapacity = this.currentCapacity + 1;
    }

    public void sub() {
        this.currentCapacity = this.currentCapacity - 1;
    }
}
