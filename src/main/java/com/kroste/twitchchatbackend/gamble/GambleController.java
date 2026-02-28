package com.kroste.twitchchatbackend.gamble;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/gambling")
public class GambleController {

    public final GambleService gambleService;

    @PostMapping("/play")
    public ResponseEntity<Gamble> playGamble(@RequestParam String twitchName, @Positive @RequestParam int stake) {
        return ResponseEntity.ok(gambleService.playGamble(twitchName, stake));
    }
}
