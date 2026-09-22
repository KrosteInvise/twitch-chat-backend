package com.kroste.twitchchatbackend.fishing;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FishingTripRepository extends JpaRepository<FishingTripEntity, Long> {

    Optional<FishingTripEntity> findByTwitchName(String twitchName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<FishingTripEntity> findByResolvesAtLessThanEqualOrderByTwitchNameAsc(Instant resolvesAt);
}
