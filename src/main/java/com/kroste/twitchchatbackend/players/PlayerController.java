package com.kroste.twitchchatbackend.players;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/players")
public class PlayerController {

    public final PlayerService playerService;

    @PostMapping("/create")
    public ResponseEntity<PlayerEntity> createPlayer(@Valid @RequestBody PlayerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(request.twitchName(), request.gold()));
    }

    @GetMapping
    public ResponseEntity<List<PlayerEntity>> findAllPlayers() {
        return ResponseEntity.ok(playerService.findAllPlayers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerEntity> findPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.findPlayerById(id));
    }

    @GetMapping("/by-name/{twitchName}")
    public PlayerEntity findPlayerByTwitchName(@PathVariable String twitchName) {
        return playerService.findPlayerByTwitchName(twitchName);
    }

    @PutMapping("/{twitchName}/gold")
    public ResponseEntity<Void> updatePlayerGold(@PathVariable String twitchName, @RequestParam Integer amount) {
        playerService.updateGoldByTwitchName(twitchName, amount);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{twitchName}/gold")
    public ResponseEntity<Void> modifyPlayerGold(@PathVariable String twitchName, @RequestParam Integer amount) {
        playerService.modifyGoldByTwitchName(twitchName, amount);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerById(@PathVariable Long id) {
        playerService.deletePlayerById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-name/{twitchName}")
    public ResponseEntity<Void> deletePlayerByTwitchName(@PathVariable String twitchName) {
        playerService.deletePlayerByTwitchName(twitchName);
        return ResponseEntity.noContent().build();
    }
}