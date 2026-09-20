package com.kroste.twitchchatbackend.fishing;

import java.util.Optional;

public record CatchRoll(
        boolean caught,
        FishSpecies species,
        Mutation mutation,
        int catchValue,
        int score
) {
    public static CatchRoll miss() {
        return new CatchRoll(false, null, null, 0, 0);
    }

    public static CatchRoll of(FishSpecies species, Mutation mutation) {
        double multiplier = mutation == null ? 1.0 : mutation.priceMultiplier();
        int catchValue = (int) Math.round(species.basePrice() * multiplier);
        int score = species.rarity().rank() * 1000 + catchValue;
        return new CatchRoll(true, species, mutation, catchValue, score);
    }

    public Optional<Mutation> mutationOptional() {
        return Optional.ofNullable(mutation);
    }
}
