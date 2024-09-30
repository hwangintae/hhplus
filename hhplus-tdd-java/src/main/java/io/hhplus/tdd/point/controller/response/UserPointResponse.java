package io.hhplus.tdd.point.controller.response;

import io.hhplus.tdd.entity.UserPoint;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserPointResponse {
    private final Long id;
    private final Long point;

    @Builder
    protected UserPointResponse(Long id, Long point) {
        this.id = id;
        this.point = point;
    }

    public static UserPointResponse of(UserPoint userPoint) {
        return UserPointResponse.builder()
                .id(userPoint.id())
                .point(userPoint.point())
                .build();
    }
}
