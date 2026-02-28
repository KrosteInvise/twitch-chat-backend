package com.kroste.twitchchatbackend.gamble;

public record Gamble(
        String twitchName,
        int playerRoll,
        int botRoll,
        int stake
) {
}
