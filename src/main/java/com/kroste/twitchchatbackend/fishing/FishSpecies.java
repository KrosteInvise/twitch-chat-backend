package com.kroste.twitchchatbackend.fishing;

public record FishSpecies(
        String id,
        String name,
        Rarity rarity,
        int basePrice,
        int weight
) {
}
