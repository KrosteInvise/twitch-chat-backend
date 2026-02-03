package com.kroste.twitch_chat_backend.controllers;

import com.kroste.twitch_chat_backend.dto.GoldTransactionRequest;
import com.kroste.twitch_chat_backend.entities.PlayerEntity;
import com.kroste.twitch_chat_backend.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/players")
public class PlayerController {

    public final PlayerService playerService;

    @PostMapping("/create")
    public ResponseEntity<PlayerEntity> create(@RequestBody PlayerEntity player) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playerService.create(player));
    }

    @GetMapping
    public ResponseEntity<List<PlayerEntity>> findAll() {
        return ResponseEntity.ok(playerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerEntity> findById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.findById(id));
    }

    @GetMapping("/by-name/{twitchName}")
    public PlayerEntity findByTwitchName(@PathVariable String twitchName) {
        return playerService.findByTwitchName(twitchName);
    }

    @PutMapping("/{twitchName}/gold")
    public ResponseEntity<Void> updateGold(@PathVariable String twitchName, @RequestBody GoldTransactionRequest request) {
        playerService.updateGold(twitchName, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{twitchName}/gold")
    public ResponseEntity<Void> modifyGold(@PathVariable String twitchName, @RequestBody GoldTransactionRequest request) {
        playerService.modifyGold(twitchName, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        playerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-name/{twitchName}")
    public ResponseEntity<Void> deleteByName(@PathVariable String twitchName) {
        playerService.deleteByTwitchName(twitchName);
        return ResponseEntity.noContent().build();
    }
}