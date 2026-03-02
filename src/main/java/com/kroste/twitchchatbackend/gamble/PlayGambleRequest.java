package com.kroste.twitchchatbackend.gamble;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PlayGambleRequest(
        @NotBlank @Size(min = 3) String twitchName,
        @Positive int stake
) {
}