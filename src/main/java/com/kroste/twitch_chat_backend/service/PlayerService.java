package com.kroste.twitch_chat_backend.service;

import com.kroste.twitch_chat_backend.entities.Player;
import com.kroste.twitch_chat_backend.entities.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {

    public final PlayerRepository playerRepository;

    public Player create(Player player) {
        return playerRepository.save(player);
    }

    public Player readById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Player with id " + id + " does not exist!"));
    }

    public Player readByTwitchName(String twitchName) {
        return playerRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new IllegalStateException("Player with name " + twitchName + " does not exist!"));
    }

    public List<Player> readAll() {
        return playerRepository.findAll();
    }

    public Player updateGold(String twitchName, Integer newGold) {
        Player player = readByTwitchName(twitchName);
        player.setGold(newGold);
        return playerRepository.save(player);
    }

    public void deleteById(Long id) {
        playerRepository.deleteById(id);
    }

    public void deleteByTwitchName(String twitchName) {
        playerRepository.deleteByTwitchName(twitchName);
    }
}
