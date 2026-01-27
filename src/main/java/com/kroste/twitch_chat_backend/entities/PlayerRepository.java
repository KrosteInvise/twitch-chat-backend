package com.kroste.twitch_chat_backend.entities;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByTwitchName(String twitchName);

    boolean existsByTwitchName(String twitchName);

    void deleteByTwitchName(String twitchName);
}
