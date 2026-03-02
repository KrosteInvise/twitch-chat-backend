package com.kroste.twitchchatbackend.gamble;

import com.kroste.twitchchatbackend.players.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.random.RandomGenerator;

@Service
@Transactional
@RequiredArgsConstructor
public class GambleService {

    private final PlayerMapper playerMapper;
    private final PlayerRepository playerRepository;

    public Gamble playGamble(String twitchName, int stake) {

        PlayerEntity playerEntity = playerRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with name " + twitchName + " does not exist!"));
        Player player = playerMapper.toDomain(playerEntity);

        int playerRoll = RandomGenerator.getDefault().nextInt(1, 13);
        int botRoll = RandomGenerator.getDefault().nextInt(1, 13);

        player.playGamble(stake, playerRoll, botRoll);
        playerMapper.updateEntityFromDomain(player, playerEntity);

        return new Gamble(twitchName, playerRoll, botRoll, stake);
    }
}