package com.kroste.twitch_chat_backend.service;

import com.kroste.twitch_chat_backend.entities.Player;
import com.kroste.twitch_chat_backend.entities.PlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerService {

    public final PlayerRepository playerRepository;

    public Player create(Player player) {
        playerRepository.findByTwitchName(player.getTwitchName())
                .ifPresent(p -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Player with name " + player.getTwitchName() + " already exists!");
                });
        return playerRepository.save(player);
    }

    public Player readById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with id " + id + " does not exist!"));
    }

    public Player readByTwitchName(String twitchName) {
        return playerRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with name " + twitchName + " does not exist!"));
    }

    public List<Player> readAll() {
        return playerRepository.findAll();
    }

    public void updateGold(String twitchName, Integer newGold) {
        Player player = readByTwitchName(twitchName);
        player.setGold(newGold);
        playerRepository.save(player);
    }
    
    public void deleteById(Long id) {
        readById(id);
        playerRepository.deleteById(id);
    }

    public void deleteByTwitchName(String twitchName) {
        readByTwitchName(twitchName);
        playerRepository.deleteByTwitchName(twitchName);
    }
}
