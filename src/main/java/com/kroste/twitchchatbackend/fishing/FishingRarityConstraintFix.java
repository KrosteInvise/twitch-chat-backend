package com.kroste.twitchchatbackend.fishing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Hibernate ddl-auto=update created enum CHECKs that do not grow when {@link Rarity} does.
 * Rarity is now mapped as VARCHAR; drop any leftover checks so values like MYTHIC can insert.
 */
@Component
public class FishingRarityConstraintFix implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(FishingRarityConstraintFix.class);

    private static final String[] DROP_STATEMENTS = {
            "ALTER TABLE fishing_trips DROP CONSTRAINT IF EXISTS fishing_trips_rarity_check",
            "ALTER TABLE fishing_trophies DROP CONSTRAINT IF EXISTS fishing_trophies_rarity_check",
            "ALTER TABLE fishing_pending_catches DROP CONSTRAINT IF EXISTS fishing_pending_catches_rarity_check"
    };

    private final JdbcTemplate jdbcTemplate;

    public FishingRarityConstraintFix(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (String sql : DROP_STATEMENTS) {
            jdbcTemplate.execute(sql);
        }
        log.info("Ensured fishing rarity CHECK constraints are dropped");
    }
}
