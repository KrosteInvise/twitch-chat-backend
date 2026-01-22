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

    public List<Player> readAll() {
        return playerRepository.findAll();
    }

    public void update(Long id, Integer gold) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if(optionalPlayer.isEmpty())
            throw new IllegalStateException("User with id " + id + " does not exist!");

        Player player = optionalPlayer.get();

        player.setGold(gold);

        playerRepository.save(player);
    }

    public void delete(Long id) {
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if(optionalPlayer.isEmpty())
            throw new IllegalStateException("User with id: " + id + " does not exist!");
        playerRepository.deleteById(id);
    }
}
