package com.kroste.twitchchatbackend.fishing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FishingPlayerRequest(
        @NotBlank @Size(min = 3) String twitchName
) {
}
