package com.kroste.twitchchatbackend.fishing;

import java.util.List;

public record BiteResponse(
        String twitchName,
        boolean caught,
        String speciesId,
        String speciesName,
        Rarity rarity,
        List<MutationView> mutations,
        Integer catchValue,
        Integer score,
        int goldDelta,
        int newBalance,
        boolean newTrophy
) {
}
