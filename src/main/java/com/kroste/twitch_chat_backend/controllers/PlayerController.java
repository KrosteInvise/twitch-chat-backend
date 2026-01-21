package com.kroste.twitch_chat_backend.controllers;

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

    @PostMapping
    public Player create(@RequestBody Player player) {
        return playerService.create(player);
    }

    @GetMapping
    public List<Player> read() {
        return playerService.readAll();
    }

    @PutMapping(path = "{id}")
    public void update(@PathVariable Long id, @RequestParam Integer gold) {
        playerService.update(id, gold);
    }

    @DeleteMapping(path = "{id}")
    public void delete(@PathVariable Long id) {
        playerService.delete(id);
    }
}
