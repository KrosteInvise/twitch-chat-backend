package com.kroste.twitch_chat_backend.controllers;

import com.kroste.twitch_chat_backend.entities.Player;
import com.kroste.twitch_chat_backend.service.PlayerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/players")
public class PlayerController {

    public final PlayerService playerService;

    @PostMapping
    public Player create(@RequestBody Player player) {
        return playerService.create(player);
    }

    @GetMapping
    public List<Player> readAll() {
        return playerService.readAll();
    }

    @GetMapping("{id}")
    public Player readById(Long id) {
        return playerService.readById(id);
    }

    @GetMapping("by-name/{twitchName}")
    public Player readByTwitchName(@PathVariable String twitchName) {
        return playerService.readByTwitchName(twitchName);
    }

    @PutMapping("{twitchName}/gold")
    public void updateGold(@PathVariable String twitchName, @RequestParam Integer gold) {
        playerService.updateGold(twitchName, gold);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable Long id) {
        playerService.deleteById(id);
    }

    @DeleteMapping("by-name/{twitchName}")
    public void deleteByName(@PathVariable String twitchName) {
        playerService.deleteByTwitchName(twitchName);
    }
}
