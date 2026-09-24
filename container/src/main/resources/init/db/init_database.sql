CREATE TABLE IF NOT EXISTS players (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    twitch_name VARCHAR(255) NOT NULL UNIQUE,
    gold INTEGER NOT NULL
);

-- If an old DB still has fishing_*_rarity_check, run fix_rarity_check.sql once
-- (or restart the app: FishingRarityConstraintFix drops them on startup).

CREATE TABLE IF NOT EXISTS fishing_pending_catches (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    twitch_name VARCHAR(255) NOT NULL UNIQUE,
    species_id VARCHAR(255) NOT NULL,
    species_name VARCHAR(255) NOT NULL,
    rarity VARCHAR(32) NOT NULL,
    mutation_id VARCHAR(255),
    mutation_name VARCHAR(255),
    mutation_multiplier DOUBLE PRECISION,
    catch_value INTEGER NOT NULL,
    score INTEGER NOT NULL,
    rerolls_used INTEGER NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS fishing_trips (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    twitch_name VARCHAR(255) NOT NULL UNIQUE,
    caught BOOLEAN NOT NULL,
    species_id VARCHAR(255),
    species_name VARCHAR(255),
    rarity VARCHAR(32),
    mutation_id VARCHAR(255),
    mutation_name VARCHAR(255),
    mutation_multiplier DOUBLE PRECISION,
    catch_value INTEGER NOT NULL,
    score INTEGER NOT NULL,
    resolves_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS fishing_trophies (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    twitch_name VARCHAR(255) NOT NULL UNIQUE,
    species_id VARCHAR(255) NOT NULL,
    species_name VARCHAR(255) NOT NULL,
    rarity VARCHAR(32) NOT NULL,
    mutation_id VARCHAR(255),
    mutation_name VARCHAR(255),
    mutation_multiplier DOUBLE PRECISION,
    catch_value INTEGER NOT NULL,
    score INTEGER NOT NULL,
    caught_at TIMESTAMPTZ NOT NULL
);
