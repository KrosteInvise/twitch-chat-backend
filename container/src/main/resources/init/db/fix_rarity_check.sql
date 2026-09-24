-- Hibernate ddl-auto=update creates enum CHECKs that do not update when Rarity grows.
-- Entities now map rarity as VARCHAR; drop stale checks on existing DBs if needed.
ALTER TABLE fishing_trips DROP CONSTRAINT IF EXISTS fishing_trips_rarity_check;
ALTER TABLE fishing_trophies DROP CONSTRAINT IF EXISTS fishing_trophies_rarity_check;
ALTER TABLE fishing_pending_catches DROP CONSTRAINT IF EXISTS fishing_pending_catches_rarity_check;
