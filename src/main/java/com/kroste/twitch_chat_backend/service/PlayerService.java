package com.kroste.twitch_chat_backend.service;

import com.kroste.twitch_chat_backend.dto.GoldTransactionRequest;
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

    public Player findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with id " + id + " does not exist!"));
    }

    public Player findByTwitchName(String twitchName) {
        return playerRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with name " + twitchName + " does not exist!"));
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public void updateGold(String twitchName, Integer newGold) {
        Player player = findByTwitchName(twitchName);
        player.setGold(newGold);
        playerRepository.save(player);
    }

    public void modifyGold(String twitchName, Integer amount) {
        Player player = findByTwitchName(twitchName);

        int newBalance = player.getGold() + amount;

        if(newBalance < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Balance is below zero");
        }

        player.setGold(newBalance);
        playerRepository.save(player);
    }
    
    public void deleteById(Long id) {
        findById(id);
        playerRepository.deleteById(id);
    }

    public void deleteByTwitchName(String twitchName) {
        findByTwitchName(twitchName);
        playerRepository.deleteByTwitchName(twitchName);
    }
}
