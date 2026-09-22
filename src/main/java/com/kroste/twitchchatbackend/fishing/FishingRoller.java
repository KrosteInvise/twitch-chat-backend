package com.kroste.twitchchatbackend.fishing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

@Component
public class FishingRoller {

    private final FishCatalog catalog;
    private final RandomGenerator random;

    @Autowired
    public FishingRoller(FishCatalog catalog) {
        this(catalog, RandomGenerator.getDefault());
    }

    /** Package-private for deterministic unit tests. */
    FishingRoller(FishCatalog catalog, RandomGenerator random) {
        this.catalog = catalog;
        this.random = random;
    }

    public CatchRoll roll() {
        if (random.nextDouble() < FishingProperties.MISS_CHANCE) {
            return CatchRoll.miss();
        }

        FishSpecies species = rollSpecies();
        Mutation mutation = rollMutation().orElse(null);
        return CatchRoll.of(species, mutation);
    }

    FishSpecies rollSpecies() {
        int totalWeight = catalog.totalSpeciesWeight();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (FishSpecies species : catalog.species()) {
            cumulative += species.weight();
            if (roll < cumulative) {
                return species;
            }
        }
        return catalog.species().getLast();
    }

    Optional<Mutation> rollMutation() {
        double roll = random.nextDouble();
        double cumulative = 0;
        List<Mutation> mutations = catalog.mutations();
        for (Mutation mutation : mutations) {
            cumulative += mutation.chance();
            if (roll < cumulative) {
                return Optional.of(mutation);
            }
        }
        return Optional.empty();
    }

    public int rollWaitMinutes(int minMinutes, int maxMinutes) {
        int min = Math.max(0, Math.min(minMinutes, maxMinutes));
        int max = Math.max(min, Math.max(minMinutes, maxMinutes));
        return min + random.nextInt(max - min + 1);
    }
}
