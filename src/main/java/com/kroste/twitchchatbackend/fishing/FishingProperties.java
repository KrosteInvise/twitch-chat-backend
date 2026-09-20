package com.kroste.twitchchatbackend.fishing;

public final class FishingProperties {

    /** Chance that a cast misses entirely (no pending catch). */
    public static final double MISS_CHANCE = 0.18;

    public static final int CAST_COST = 5;
    public static final int REROLL_COST = 40;
    public static final int MAX_REROLLS = 2;
    public static final int PENDING_TTL_SECONDS = 60;

    private FishingProperties() {
    }
}
