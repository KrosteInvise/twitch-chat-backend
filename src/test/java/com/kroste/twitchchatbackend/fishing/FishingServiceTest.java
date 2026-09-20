package com.kroste.twitchchatbackend.fishing;

import com.kroste.twitchchatbackend.players.Player;
import com.kroste.twitchchatbackend.players.PlayerEntity;
import com.kroste.twitchchatbackend.players.PlayerMapper;
import com.kroste.twitchchatbackend.players.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FishingServiceTest {

    @Mock
    PlayerRepository playerRepository;
    @Mock
    PlayerMapper playerMapper;
    @Mock
    PendingCatchRepository pendingCatchRepository;
    @Mock
    FishingTrophyRepository fishingTrophyRepository;
    @Mock
    FishingRoller fishingRoller;
    @Mock
    FishCatalog fishCatalog;

    @InjectMocks
    FishingService fishingService;

    @Test
    void castMissChargesCostAndDoesNotCreatePending() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        Player player = new Player(name, 100);

        when(playerRepository.findByTwitchName(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(pendingCatchRepository.findByTwitchName(name)).thenReturn(Optional.empty());
        when(fishingRoller.roll()).thenReturn(CatchRoll.miss());

        CastResponse response = fishingService.cast(name);

        assertFalse(response.caught());
        assertEquals(-FishingProperties.CAST_COST, response.goldDelta());
        assertEquals(95, response.newBalance());
        verify(pendingCatchRepository, never()).save(any());
        verify(playerRepository).save(entity);
    }

    @Test
    void castSuccessCreatesPendingWithoutPayingCatchValueYet() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        Player player = new Player(name, 100);
        FishSpecies carp = new FishSpecies("carp", "Карп", Rarity.COMMON, 15, 35);

        when(playerRepository.findByTwitchName(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(pendingCatchRepository.findByTwitchName(name)).thenReturn(Optional.empty());
        when(fishingRoller.roll()).thenReturn(CatchRoll.of(carp, null));
        when(fishCatalog.findSpeciesById("carp")).thenReturn(Optional.of(carp));
        when(pendingCatchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CastResponse response = fishingService.cast(name);

        assertTrue(response.caught());
        assertTrue(response.pending());
        assertEquals(15, response.catchValue());
        assertEquals(95, response.newBalance());
        assertEquals(2, response.rerollsRemaining());

        ArgumentCaptor<PendingCatchEntity> captor = ArgumentCaptor.forClass(PendingCatchEntity.class);
        verify(pendingCatchRepository).save(captor.capture());
        assertEquals("carp", captor.getValue().getSpeciesId());
        assertEquals(0, captor.getValue().getRerollsUsed());
    }

    @Test
    void castThrowsWhenNotEnoughGold() {
        String name = "broke";
        PlayerEntity entity = new PlayerEntity(1L, name, 2);
        Player player = new Player(name, 2);

        when(playerRepository.findByTwitchName(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(pendingCatchRepository.findByTwitchName(name)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> fishingService.cast(name));
        verify(fishingRoller, never()).roll();
    }

    @Test
    void keepAwardsCatchValueAndUpdatesTrophy() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 50);
        Player player = new Player(name, 50);

        PendingCatchEntity pending = new PendingCatchEntity();
        pending.setTwitchName(name);
        pending.setSpeciesId("carp");
        pending.setSpeciesName("Карп");
        pending.setRarity(Rarity.COMMON);
        pending.setCatchValue(15);
        pending.setScore(1015);
        pending.setRerollsUsed(0);
        pending.setExpiresAt(Instant.now().plusSeconds(30));

        when(playerRepository.findByTwitchName(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(pendingCatchRepository.findByTwitchName(name)).thenReturn(Optional.of(pending));
        when(fishingTrophyRepository.findByTwitchName(name)).thenReturn(Optional.empty());

        KeepResponse response = fishingService.keep(name);

        assertTrue(response.kept());
        assertEquals(15, response.goldDelta());
        assertEquals(65, response.newBalance());
        assertTrue(response.newTrophy());
        verify(fishingTrophyRepository).save(any(FishingTrophyEntity.class));
        verify(pendingCatchRepository).delete(pending);
    }

    @Test
    void rerollPuffDeletesPendingWithoutCatchPayout() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        Player player = new Player(name, 100);

        PendingCatchEntity pending = new PendingCatchEntity();
        pending.setTwitchName(name);
        pending.setSpeciesId("carp");
        pending.setSpeciesName("Карп");
        pending.setRarity(Rarity.COMMON);
        pending.setCatchValue(15);
        pending.setScore(1015);
        pending.setRerollsUsed(0);
        pending.setExpiresAt(Instant.now().plusSeconds(30));

        when(playerRepository.findByTwitchName(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(pendingCatchRepository.findByTwitchName(name)).thenReturn(Optional.of(pending));
        when(fishingRoller.rollRerollOutcome()).thenReturn(RerollOutcome.PUFF);

        RerollResponse response = fishingService.reroll(name);

        assertEquals(RerollOutcome.PUFF, response.outcome());
        assertTrue(response.puffed());
        assertFalse(response.pending());
        assertEquals(100 - FishingProperties.REROLL_COST, response.newBalance());
        verify(pendingCatchRepository).delete(pending);
        verify(fishingTrophyRepository, never()).save(any());
    }
}
