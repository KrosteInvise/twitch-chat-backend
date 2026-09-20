package com.kroste.twitchchatbackend.fishing;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("api/fishing")
public class FishingController {

    private final FishingService fishingService;

    @PostMapping("/cast")
    public ResponseEntity<CastResponse> cast(@Valid @RequestBody FishingPlayerRequest request) {
        return ResponseEntity.ok(fishingService.cast(request.twitchName()));
    }

    @PostMapping("/keep")
    public ResponseEntity<KeepResponse> keep(@Valid @RequestBody FishingPlayerRequest request) {
        return ResponseEntity.ok(fishingService.keep(request.twitchName()));
    }

    @PostMapping("/reroll")
    public ResponseEntity<RerollResponse> reroll(@Valid @RequestBody FishingPlayerRequest request) {
        return ResponseEntity.ok(fishingService.reroll(request.twitchName()));
    }

    @GetMapping("/trophy/{twitchName}")
    public ResponseEntity<TrophyResponse> trophy(@PathVariable String twitchName) {
        return ResponseEntity.ok(fishingService.getTrophy(twitchName));
    }

    @GetMapping("/top")
    public ResponseEntity<List<TrophyResponse>> top(
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int limit
    ) {
        return ResponseEntity.ok(fishingService.getTop(limit));
    }
}
