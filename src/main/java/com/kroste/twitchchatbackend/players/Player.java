package com.kroste.twitchchatbackend.players;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
@Setter
public class Player {

    private Long id;
    private String twitchName;
    private int gold;

    public Player(String twitchName, int gold) {
        this.twitchName = twitchName;
        this.gold = gold;
    }

    public void changeBalance(Integer amount) {
        if(amount == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction sum can't be null!");
        }

        if(this.gold + amount < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough gold! Balance: " + gold);
        }

        this.gold += amount;
    }

    public void playGamble(int stake, int playerRoll, int botRoll) {
        if (this.gold < stake) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough gold! Balance: " + gold);
        }

        if (playerRoll > botRoll) {
            this.gold += stake;
        } else if (playerRoll < botRoll) {
            this.gold -= stake;
        }
    }
}
