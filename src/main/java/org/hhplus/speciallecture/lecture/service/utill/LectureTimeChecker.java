package org.hhplus.speciallecture.lecture.service.utill;

import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LectureTimeChecker {

    public boolean isOverlaps(LectureDomain registeredLecture, LectureDomain checkLecture) {
        LocalDateTime startTime = registeredLecture.getLectureStartDateTime();
        LocalDateTime endTime = registeredLecture.getLectureEndDateTime();

        LocalDateTime checkStartTime = checkLecture.getLectureStartDateTime();
        LocalDateTime checkEndTime = checkLecture.getLectureEndDateTime();

        List<Boolean> booleans = List.of(
                (startTime.isBefore(checkStartTime) && endTime.isAfter(checkStartTime)),
                (startTime.isBefore(checkEndTime) && endTime.isAfter(checkEndTime)),
                startTime.isEqual(checkStartTime),
                endTime.isEqual(checkEndTime)
        );

        return booleans.stream().anyMatch(c -> c);
    }
}
