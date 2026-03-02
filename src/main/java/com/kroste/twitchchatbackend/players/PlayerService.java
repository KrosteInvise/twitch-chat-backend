package com.kroste.twitchchatbackend.players;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlayerService {

    public final PlayerRepository playerRepository;
    public final PlayerMapper playerMapper;

    public PlayerEntity createPlayer(String twitchName, int gold) {
        Player player = new Player(twitchName, gold);
        playerRepository.findByTwitchName(player.getTwitchName())
                .ifPresent(p -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Player with name " + player.getTwitchName() + " already exists!");
                });
        return playerRepository.save(playerMapper.toEntity(player));
    }

    @Transactional(readOnly = true)
    public PlayerEntity findPlayerById(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with id " + id + " does not exist!"));
    }

    @Transactional(readOnly = true)
    public PlayerEntity findPlayerByTwitchName(String twitchName) {
        return playerRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player with name " + twitchName + " does not exist!"));
    }

    @Transactional(readOnly = true)
    public List<PlayerEntity> findAllPlayers() {
        return playerRepository.findAll();
    }

    public void updateGoldByTwitchName(String twitchName, int amount) {
        PlayerEntity entity = findPlayerByTwitchName(twitchName);

        Player player = playerMapper.toDomain(entity);
        player.setGold(amount);

        playerMapper.updateEntityFromDomain(player, entity);
        playerRepository.save(entity);
    }

    public void modifyGoldByTwitchName(String twitchName, int amount) {
        PlayerEntity entity = findPlayerByTwitchName(twitchName);

        Player player = playerMapper.toDomain(entity);
        player.changeBalance(amount);

        playerMapper.updateEntityFromDomain(player, entity);
        playerRepository.save(entity);
    }
    
    public void deletePlayerById(Long id) {
        playerRepository.deleteById(id);
    }

    public void deletePlayerByTwitchName(String twitchName) {
        playerRepository.deleteByTwitchName(twitchName);
    }
}