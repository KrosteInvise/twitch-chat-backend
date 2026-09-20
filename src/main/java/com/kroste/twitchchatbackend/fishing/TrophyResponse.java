package com.kroste.twitchchatbackend.fishing;

import java.time.Instant;
import java.util.List;

public record TrophyResponse(
        String twitchName,
        String speciesId,
        String speciesName,
        Rarity rarity,
        List<MutationView> mutations,
        int catchValue,
        int score,
        Instant caughtAt
) {
}
