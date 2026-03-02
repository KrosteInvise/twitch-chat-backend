package com.kroste.twitchchatbackend.players;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record PlayerCreateRequest(
        @NotBlank
        @Size(min = 3)
        String twitchName,
        @PositiveOrZero
        int gold
) {
}
