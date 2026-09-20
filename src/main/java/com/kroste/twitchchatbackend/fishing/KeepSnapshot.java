package com.kroste.twitchchatbackend.fishing;

import java.util.List;

public record KeepSnapshot(
        String speciesId,
        String speciesName,
        Rarity rarity,
        List<MutationView> mutations,
        int catchValue,
        int score,
        boolean newTrophy,
        int goldDelta,
        int newBalance
) {
}
