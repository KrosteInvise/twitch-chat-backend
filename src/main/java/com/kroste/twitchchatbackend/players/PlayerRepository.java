package com.kroste.twitchchatbackend.players;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findByTwitchName(String twitchName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PlayerEntity p WHERE p.twitchName = :twitchName")
    Optional<PlayerEntity> findByTwitchNameForUpdate(String twitchName);

    boolean existsByTwitchName(String twitchName);

    void deleteByTwitchName(String twitchName);
}
