package org.hhplus.speciallecture.lecture.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hhplus.speciallecture.common.ApiResponse;
import org.hhplus.speciallecture.common.exception.LectureTimeValidException;
import org.hhplus.speciallecture.lecture.controller.request.LectureCreate;
import org.hhplus.speciallecture.lecture.controller.request.LectureEnrollmentRequest;
import org.hhplus.speciallecture.lecture.domain.lecture.LectureDomain;
import org.hhplus.speciallecture.lecture.service.lecture.LectureService;
import org.hhplus.speciallecture.lecture.service.lecture.response.LectureServiceResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("/api/lecture")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @GetMapping
    public ApiResponse<List<LectureServiceResponse>> getLectures() {
        LocalDateTime now = LocalDateTime.now();

        List<LectureServiceResponse> lectures = lectureService.getLectures(now);

        return ApiResponse.ok(lectures);
    }

    @PostMapping
    public ApiResponse<LectureDomain> save(@RequestBody @Valid LectureCreate create) {
        if (!create.validate(LocalDateTime.now())) {
            throw new LectureTimeValidException();
        }

        LectureDomain save = lectureService.save(create.toDomain());

        return ApiResponse.ok(save);
    }

    @GetMapping("/enroll/{studentId}")
    public ApiResponse<List<LectureServiceResponse>> enrollLecture(@PathVariable Long studentId) {
        List<LectureServiceResponse> lectures = lectureService.getLectures(studentId);

        return ApiResponse.ok(lectures);
    }

    @PostMapping("/enroll")
    public ApiResponse<String> enrollLecture(@RequestBody LectureEnrollmentRequest request) {
        lectureService.enrollLecture(request.getStudentId(), request.getLectureId());

        return ApiResponse.ok("create");
    }


}
