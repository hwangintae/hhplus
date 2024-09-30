package io.hhplus.tdd.point.controller.response;

import io.hhplus.tdd.entity.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointHistoriesResponse {

    private final Long userId;
    private final Long amount;
    private final TransactionType type;

    @Builder
    protected PointHistoriesResponse(Long userId, Long amount, TransactionType type) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public static PointHistoriesResponse of(PointHistory pointHistory) {
        return PointHistoriesResponse.builder()
                .userId(pointHistory.userId())
                .amount(pointHistory.amount())
                .type(pointHistory.type())
                .build();
    }
}
