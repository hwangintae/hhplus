package io.hhplus.tdd.entity;

import io.hhplus.tdd.exception.MaxChargeException;
import io.hhplus.tdd.exception.MinChargeException;
import io.hhplus.tdd.exception.MinusPointException;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    private static final Long MAX_CHARGE = 50_000_000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint add(long amount) {
        long addPoint = this.point + amount;

        if (amount < 1) {
            throw new MinChargeException();
        }

        if (addPoint > MAX_CHARGE) {
            throw new MaxChargeException();
        }

        return new UserPoint(id, addPoint, System.currentTimeMillis());
    }

    public UserPoint sub(long amount) {
        long subPoint = this.point - amount;

        if (amount < 1) {
            throw new MinChargeException();
        }

        if (subPoint < 0) {
            throw new MinusPointException();
        }

        return new UserPoint(id, subPoint, System.currentTimeMillis());
    }

}
