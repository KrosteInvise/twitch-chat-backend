package com.kroste.twitch_chat_backend;

import com.kroste.twitchchatbackend.players.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerMapper playerMapper;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void shouldModifyGoldSuccessfully() {
        String name = "twitch_user";
        Integer amount = 50;

        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        Player player = new Player(name, 100);

        when(playerRepository.findByTwitchName(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);

        playerService.modifyGoldByTwitchName(name, amount);

        assertEquals(150, player.getGold());
        verify(playerMapper).updateEntityFromDomain(player, entity);
        verify(playerRepository).save(entity);
    }

    @Test
    void shouldThrowExceptionWhenNotEnoughGold() {
        String name = "twitch_user";
        Integer gold = -1000;

        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        Player domainPlayer = new Player(name, 100);

        when(playerRepository.findByTwitchName(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(domainPlayer);

        assertThrows(ResponseStatusException.class, () -> {
            playerService.modifyGoldByTwitchName(name, gold);
        });

        verify(playerRepository, never()).save(any());
    }
}
