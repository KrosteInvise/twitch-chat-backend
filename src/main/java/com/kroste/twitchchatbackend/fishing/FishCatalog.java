package com.kroste.twitchchatbackend.fishing;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FishCatalog {

    private final List<FishSpecies> species = List.of(
            new FishSpecies("minnow", "Пескарь", Rarity.COMMON, 10, 40),
            new FishSpecies("carp", "Карп", Rarity.COMMON, 15, 35),
            new FishSpecies("perch", "Окунь", Rarity.COMMON, 12, 30),
            new FishSpecies("roach", "Плотва", Rarity.COMMON, 11, 28),

            new FishSpecies("pike", "Щука", Rarity.UNCOMMON, 30, 14),
            new FishSpecies("trout", "Форель", Rarity.UNCOMMON, 35, 12),
            new FishSpecies("bream", "Лещ", Rarity.UNCOMMON, 28, 12),

            new FishSpecies("golden_carp", "Золотой карп", Rarity.RARE, 80, 5),
            new FishSpecies("catfish", "Сом", Rarity.RARE, 90, 4),

            new FishSpecies("sturgeon", "Осётр", Rarity.EPIC, 200, 2),
            new FishSpecies("electric_eel", "Угорь", Rarity.EPIC, 220, 2),

            new FishSpecies("rainbow_koi", "Радужный кои", Rarity.LEGENDARY, 500, 1),
            new FishSpecies("leviathan", "Левиафан", Rarity.LEGENDARY, 550, 1)
    );

    private final List<Mutation> mutations = List.of(
            new Mutation("shiny", "Shiny", 0.05, 2.0),
            new Mutation("giant", "Giant", 0.04, 1.5),
            new Mutation("albino", "Albino", 0.03, 1.8),
            new Mutation("two_headed", "Two-Headed", 0.02, 2.5),
            new Mutation("ghost", "Ghost", 0.01, 3.0)
    );

    public List<FishSpecies> species() {
        return species;
    }

    public List<Mutation> mutations() {
        return mutations;
    }

    public int totalSpeciesWeight() {
        return species.stream().mapToInt(FishSpecies::weight).sum();
    }

    public Optional<FishSpecies> findSpeciesById(String id) {
        return species.stream().filter(s -> s.id().equals(id)).findFirst();
    }

    public Optional<Mutation> findMutationById(String id) {
        return mutations.stream().filter(m -> m.id().equals(id)).findFirst();
    }
}
