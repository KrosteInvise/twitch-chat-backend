package com.kroste.twitchchatbackend.players;

import org.springframework.stereotype.Component;

@Component
public class PlayerMapper {

    public Player toDomain(PlayerEntity playerEntity) {
        return new Player(
                playerEntity.getTwitchName(),
                playerEntity.getGold()
        );
    }

    public PlayerEntity toEntity(Player player) {
        return new PlayerEntity(
                player.getId(),
                player.getTwitchName(),
                player.getGold()
        );
    }

    public void updateEntityFromDomain(Player player, PlayerEntity entity) {
        entity.setGold(player.getGold());
    }
}
