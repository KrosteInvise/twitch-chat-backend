package com.kroste.twitchchatbackend.fishing;

import java.util.List;

public record RerollResponse(
        RerollOutcome outcome,
        boolean puffed,
        boolean pending,
        String speciesId,
        String speciesName,
        Rarity rarity,
        List<MutationView> mutations,
        Integer catchValue,
        Integer score,
        int rerollCost,
        int goldDelta,
        int newBalance,
        Integer rerollsRemaining
) {
}
