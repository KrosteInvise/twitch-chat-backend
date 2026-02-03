package com.kroste.twitch_chat_backend.entities;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

    Optional<PlayerEntity> findByTwitchName(String twitchName);

    boolean existsByTwitchName(String twitchName);

    void deleteByTwitchName(String twitchName);
}
