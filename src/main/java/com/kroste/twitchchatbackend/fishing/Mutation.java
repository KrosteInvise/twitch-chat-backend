package com.kroste.twitchchatbackend.fishing;

public record Mutation(
        String id,
        String name,
        double chance,
        double priceMultiplier
) {
}
