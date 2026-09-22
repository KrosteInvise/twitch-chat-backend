package com.kroste.twitchchatbackend.fishing;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "fishing_trips")
public class FishingTripEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "twitch_name", nullable = false, unique = true)
    private String twitchName;

    @Column(name = "caught", nullable = false)
    private boolean caught;

    @Column(name = "species_id")
    private String speciesId;

    @Column(name = "species_name")
    private String speciesName;

    @Enumerated(EnumType.STRING)
    @Column(name = "rarity")
    private Rarity rarity;

    @Column(name = "mutation_id")
    private String mutationId;

    @Column(name = "mutation_name")
    private String mutationName;

    @Column(name = "mutation_multiplier")
    private Double mutationMultiplier;

    @Column(name = "catch_value", nullable = false)
    private Integer catchValue;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "resolves_at", nullable = false)
    private Instant resolvesAt;
}
