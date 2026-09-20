package com.kroste.twitchchatbackend.fishing;

import java.util.List;

public record KeepResponse(
        boolean kept,
        String speciesId,
        String speciesName,
        Rarity rarity,
        List<MutationView> mutations,
        int catchValue,
        int score,
        int goldDelta,
        int newBalance,
        boolean newTrophy
) {
}
