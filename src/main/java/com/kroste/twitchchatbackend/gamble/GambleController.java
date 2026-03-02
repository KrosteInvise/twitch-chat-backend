package com.kroste.twitchchatbackend.gamble;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/gambling")
public class GambleController {

    public final GambleService gambleService;

    @PostMapping("/play")
    public ResponseEntity<Gamble> playGamble(@Valid @RequestBody PlayGambleRequest request) {
        return ResponseEntity.ok(gambleService.playGamble(request.twitchName(), request.stake()));
    }
}
