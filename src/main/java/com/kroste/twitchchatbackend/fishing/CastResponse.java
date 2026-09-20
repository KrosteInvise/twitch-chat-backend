package com.kroste.twitchchatbackend.fishing;

import java.time.Instant;
import java.util.List;

public record CastResponse(
        boolean caught,
        boolean pending,
        String speciesId,
        String speciesName,
        Rarity rarity,
        List<MutationView> mutations,
        Integer catchValue,
        Integer score,
        int castCost,
        int goldDelta,
        int newBalance,
        Integer rerollsRemaining,
        Integer rerollCost,
        Instant expiresAt,
        KeepSnapshot autoKeptPrevious
) {
    public static CastResponse miss(int castCost, int newBalance, KeepSnapshot autoKeptPrevious) {
        return new CastResponse(
                false,
                false,
                null,
                null,
                null,
                List.of(),
                null,
                null,
                castCost,
                -castCost,
                newBalance,
                null,
                null,
                null,
                autoKeptPrevious
        );
    }
}
