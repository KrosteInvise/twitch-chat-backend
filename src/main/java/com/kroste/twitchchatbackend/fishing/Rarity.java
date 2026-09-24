package com.kroste.twitchchatbackend.fishing;

public enum Rarity {
    COMMON(1),
    UNCOMMON(2),
    RARE(3),
    EPIC(4),
    LEGENDARY(5),
    MYTHIC(6);

    private final int rank;

    Rarity(int rank) {
        this.rank = rank;
    }

    public int rank() {
        return rank;
    }
}
