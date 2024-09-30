package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.entity.PointHistory;
import io.hhplus.tdd.point.controller.response.PointHistoriesResponse;
import io.hhplus.tdd.point.controller.response.UserPointResponse;
import io.hhplus.tdd.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPointResponse point(
            @PathVariable long id
    ) {
        return UserPointResponse.of(pointService.getUserPoint(id));
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistoriesResponse> history(
            @PathVariable long id
    ) {
        List<PointHistory> pointHistories = pointService.getPointHistory(id);
        return pointHistories.stream()
                .map(PointHistoriesResponse::of)
                .toList();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPointResponse charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return UserPointResponse.of(pointService.charge(id, amount));
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPointResponse use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return UserPointResponse.of(pointService.use(id, amount));
    }
}
