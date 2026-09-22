package com.kroste.twitchchatbackend.fishing;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;

class FishingRollerTest {

    private final FishCatalog catalog = new FishCatalog();

    @Test
    void catchValueAndScoreWithoutMutation() {
        FishSpecies carp = new FishSpecies("carp", "Карп", Rarity.COMMON, 15, 35);
        CatchRoll roll = CatchRoll.of(carp, null);

        assertTrue(roll.caught());
        assertEquals(15, roll.catchValue());
        assertEquals(1000 + 15, roll.score());
        assertTrue(roll.mutationOptional().isEmpty());
    }

    @Test
    void catchValueAndScoreWithMutation() {
        FishSpecies carp = new FishSpecies("golden_carp", "Золотой карп", Rarity.RARE, 80, 5);
        Mutation shiny = new Mutation("shiny", "Shiny", 0.05, 2.0);
        CatchRoll roll = CatchRoll.of(carp, shiny);

        assertEquals(160, roll.catchValue());
        assertEquals(3000 + 160, roll.score());
        assertEquals(shiny, roll.mutation());
    }

    @Test
    void missRollReturnsEmptyCatch() {
        CatchRoll miss = CatchRoll.miss();
        assertFalse(miss.caught());
        assertNull(miss.species());
        assertEquals(0, miss.catchValue());
        assertEquals(0, miss.score());
    }

    @Test
    void rollerReturnsMissWhenFirstDoubleBelowMissChance() {
        ScriptedRandom random = ScriptedRandom.doubles(0.0);
        FishingRoller roller = new FishingRoller(catalog, random);

        CatchRoll roll = roller.roll();

        assertFalse(roll.caught());
    }

    @Test
    void rollerPicksFirstSpeciesWhenWeightRollIsZero() {
        // miss check fails (0.5 >= 0.18), species roll 0 → first species, mutation roll 0.99 → none
        ScriptedRandom random = new ScriptedRandom()
                .thenDouble(0.5)
                .thenInt(0)
                .thenDouble(0.99);
        FishingRoller roller = new FishingRoller(catalog, random);

        CatchRoll roll = roller.roll();

        assertTrue(roll.caught());
        assertEquals("minnow", roll.species().id());
        assertNull(roll.mutation());
        assertEquals(10, roll.catchValue());
    }

    @Test
    void rollerAppliesFirstMutationWhenRollInFirstSlice() {
        ScriptedRandom random = new ScriptedRandom()
                .thenDouble(0.5)
                .thenInt(0)
                .thenDouble(0.01);
        FishingRoller roller = new FishingRoller(catalog, random);

        CatchRoll roll = roller.roll();

        assertTrue(roll.caught());
        assertEquals("shiny", roll.mutation().id());
        assertEquals(20, roll.catchValue());
    }

    @Test
    void catalogHasSpeciesAcrossRaritiesAndPositiveWeights() {
        assertTrue(catalog.species().size() >= 8);
        assertTrue(catalog.mutations().size() >= 3);
        assertTrue(catalog.totalSpeciesWeight() > 0);
        assertTrue(catalog.species().stream().anyMatch(s -> s.rarity() == Rarity.LEGENDARY));
    }

    @Test
    void rollWaitMinutesIsInclusive() {
        ScriptedRandom random = new ScriptedRandom().thenInt(0).thenInt(15);
        FishingRoller roller = new FishingRoller(catalog, random);

        assertEquals(5, roller.rollWaitMinutes(5, 20));
        assertEquals(20, roller.rollWaitMinutes(5, 20));
    }

    @Test
    void rollMutationEmptyWhenAboveAllChances() {
        FishingRoller roller = new FishingRoller(catalog, ScriptedRandom.doubles(0.99));
        Optional<Mutation> mutation = roller.rollMutation();
        assertTrue(mutation.isEmpty());
    }

    /**
     * Minimal scripted RNG for deterministic roller tests.
     */
    private static final class ScriptedRandom implements RandomGenerator {
        private final Deque<Double> doubles = new ArrayDeque<>();
        private final Deque<Integer> ints = new ArrayDeque<>();

        static ScriptedRandom doubles(double... values) {
            ScriptedRandom random = new ScriptedRandom();
            for (double value : values) {
                random.doubles.add(value);
            }
            return random;
        }

        ScriptedRandom thenDouble(double value) {
            doubles.add(value);
            return this;
        }

        ScriptedRandom thenInt(int value) {
            ints.add(value);
            return this;
        }

        @Override
        public double nextDouble() {
            if (doubles.isEmpty()) {
                throw new IllegalStateException("No scripted doubles left");
            }
            return doubles.removeFirst();
        }

        @Override
        public int nextInt(int bound) {
            if (ints.isEmpty()) {
                throw new IllegalStateException("No scripted ints left");
            }
            int value = ints.removeFirst();
            if (value < 0 || value >= bound) {
                throw new IllegalArgumentException("Scripted int " + value + " out of bound " + bound);
            }
            return value;
        }

        @Override
        public long nextLong() {
            throw new UnsupportedOperationException();
        }
    }
}
