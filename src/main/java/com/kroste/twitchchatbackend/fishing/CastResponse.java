package com.kroste.twitchchatbackend.fishing;

import java.time.Instant;

public record CastResponse(
        CastStatus status,
        int waitMinutes,
        Instant resolvesAt,
        int castCost,
        int goldDelta,
        int newBalance,
        long remainingSeconds
) {
    public static CastResponse departed(int waitMinutes, Instant resolvesAt, int castCost, int newBalance) {
        return new CastResponse(
                CastStatus.DEPARTED,
                waitMinutes,
                resolvesAt,
                castCost,
                -castCost,
                newBalance,
                waitMinutes * 60L
        );
    }

    public static CastResponse waiting(Instant resolvesAt, long remainingSeconds, int newBalance) {
        return new CastResponse(
                CastStatus.WAITING,
                0,
                resolvesAt,
                0,
                0,
                newBalance,
                remainingSeconds
        );
    }
}
