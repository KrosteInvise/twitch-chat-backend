package com.kroste.twitch_chat_backend.controllers;

import com.kroste.twitch_chat_backend.dto.GoldTransactionRequest;
import com.kroste.twitch_chat_backend.entities.Player;
import com.kroste.twitch_chat_backend.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/players")
public class PlayerController {

    public final PlayerService playerService;

    @PostMapping("/create")
    public Player create(@RequestBody Player player) {
        return playerService.create(player);
    }

    @GetMapping
    public List<Player> readAll() {
        return playerService.findAll();
    }

    @GetMapping("/{id}")
    public Player readById(@PathVariable Long id) {
        return playerService.findById(id);
    }

    @GetMapping("/by-name/{twitchName}")
    public Player readByTwitchName(@PathVariable String twitchName) {
        return playerService.findByTwitchName(twitchName);
    }

    @PutMapping("/{twitchName}/gold")
    public void updateGold(@PathVariable String twitchName, @RequestBody GoldTransactionRequest request) {
        playerService.updateGold(twitchName, request.getAmount());
    }

    @PostMapping("/{twitchName}/gold")
    public void modifyGold(@PathVariable String twitchName, @RequestBody GoldTransactionRequest request) {
        playerService.modifyGold(twitchName, request.getAmount());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        playerService.deleteById(id);
    }

    @DeleteMapping("/by-name/{twitchName}")
    public void deleteByName(@PathVariable String twitchName) {
        playerService.deleteByTwitchName(twitchName);
    }
}