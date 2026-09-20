package com.kroste.twitchchatbackend.fishing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingCatchRepository extends JpaRepository<PendingCatchEntity, Long> {

    Optional<PendingCatchEntity> findByTwitchName(String twitchName);

    void deleteByTwitchName(String twitchName);
}
