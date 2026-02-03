package com.kroste.twitch_chat_backend.service;

import com.kroste.twitch_chat_backend.dto.GoldTransactionRequest;
import com.kroste.twitch_chat_backend.entities.PlayerEntity;
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

    public PlayerEntity create(PlayerEntity player) {
        playerRepository.findByTwitchName(player.getTwitchName())
                .ifPresent(p -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Player with name " + player.getTwitchName() + " already exists!");
                });
        return playerRepository.save(player);
    }

    public PlayerEntity findById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with id " + id + " does not exist!"));
    }

    public PlayerEntity findByTwitchName(String twitchName) {
        return playerRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with name " + twitchName + " does not exist!"));
    }

    public List<PlayerEntity> findAll() {
        return playerRepository.findAll();
    }

    public void updateGold(String twitchName, GoldTransactionRequest request) {
        PlayerEntity player = findByTwitchName(twitchName);
        player.setGold(request.getAmount());
        playerRepository.save(player);
    }

    public void modifyGold(String twitchName, GoldTransactionRequest request) {
        PlayerEntity player = findByTwitchName(twitchName);

        int newBalance = player.getGold() + request.getAmount();

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