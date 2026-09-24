package com.kroste.twitchchatbackend.fishing;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "fishing_trophies")
public class FishingTrophyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "twitch_name", nullable = false, unique = true)
    private String twitchName;

    @Column(name = "species_id", nullable = false)
    private String speciesId;

    @Column(name = "species_name", nullable = false)
    private String speciesName;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "rarity", nullable = false, length = 32)
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

    @Column(name = "caught_at", nullable = false)
    private Instant caughtAt;
}
