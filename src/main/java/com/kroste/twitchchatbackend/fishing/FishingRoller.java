package com.kroste.twitchchatbackend.fishing;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

@Component
public class FishingRoller {

    private final FishCatalog catalog;
    private final RandomGenerator random;

    public FishingRoller(FishCatalog catalog) {
        this(catalog, RandomGenerator.getDefault());
    }

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

    public Optional<Mutation> rollMutationReplacement() {
        return rollMutation();
    }

    /**
     * Weighted reroll outcomes. Approximate: puff 18%, strip 18%, same 18%, upgrade 46%.
     */
    public RerollOutcome rollRerollOutcome() {
        double roll = random.nextDouble();
        if (roll < 0.18) {
            return RerollOutcome.PUFF;
        }
        if (roll < 0.36) {
            return RerollOutcome.STRIP;
        }
        if (roll < 0.54) {
            return RerollOutcome.SAME;
        }
        return RerollOutcome.UPGRADE;
    }

    public Mutation pickMutationDifferentFrom(String currentMutationId) {
        List<Mutation> pool = catalog.mutations().stream()
                .filter(m -> currentMutationId == null || !m.id().equals(currentMutationId))
                .toList();
        if (pool.isEmpty()) {
            return catalog.mutations().getFirst();
        }
        return pool.get(random.nextInt(pool.size()));
    }
}
