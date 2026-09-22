package com.kroste.twitchchatbackend.fishing;

import com.kroste.twitchchatbackend.players.Player;
import com.kroste.twitchchatbackend.players.PlayerEntity;
import com.kroste.twitchchatbackend.players.PlayerMapper;
import com.kroste.twitchchatbackend.players.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FishingServiceTest {

    static final Instant NOW = Instant.parse("2026-09-23T00:00:00Z");

    @Mock
    PlayerRepository playerRepository;
    @Mock
    PlayerMapper playerMapper;
    @Mock
    FishingTripRepository fishingTripRepository;
    @Mock
    FishingTrophyRepository fishingTrophyRepository;
    @Mock
    FishingRoller fishingRoller;
    @Mock
    FishingSettings fishingSettings;
    @Mock
    Clock clock;

    @InjectMocks
    FishingService fishingService;

    @BeforeEach
    void setUp() {
        lenient().when(clock.instant()).thenReturn(NOW);
        lenient().when(fishingSettings.getCastCost()).thenReturn(5);
        lenient().when(fishingSettings.getMinWaitMinutes()).thenReturn(5);
        lenient().when(fishingSettings.getMaxWaitMinutes()).thenReturn(20);
    }

    @Test
    void castChargesCostAndStoresHiddenRollUntilResolveTime() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        Player player = new Player(name, 100);
        FishSpecies carp = new FishSpecies("carp", "Карп", Rarity.COMMON, 15, 35);

        when(playerRepository.findByTwitchNameForUpdate(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(fishingTripRepository.findByTwitchName(name)).thenReturn(Optional.empty());
        when(fishingRoller.rollWaitMinutes(5, 20)).thenReturn(12);
        when(fishingRoller.roll()).thenReturn(CatchRoll.of(carp, null));
        when(fishingTripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CastResponse response = fishingService.cast(name);

        assertEquals(CastStatus.DEPARTED, response.status());
        assertEquals(12, response.waitMinutes());
        assertEquals(NOW.plusSeconds(12 * 60L), response.resolvesAt());
        assertEquals(-5, response.goldDelta());
        assertEquals(95, response.newBalance());

        ArgumentCaptor<FishingTripEntity> captor = ArgumentCaptor.forClass(FishingTripEntity.class);
        verify(fishingTripRepository).save(captor.capture());
        assertEquals("carp", captor.getValue().getSpeciesId());
        assertEquals(15, captor.getValue().getCatchValue());
        assertTrue(captor.getValue().isCaught());
        verify(playerRepository).save(entity);
    }

    @Test
    void castMissStillOpensATrip() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        Player player = new Player(name, 100);

        when(playerRepository.findByTwitchNameForUpdate(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(fishingTripRepository.findByTwitchName(name)).thenReturn(Optional.empty());
        when(fishingRoller.rollWaitMinutes(5, 20)).thenReturn(5);
        when(fishingRoller.roll()).thenReturn(CatchRoll.miss());
        when(fishingTripRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CastResponse response = fishingService.cast(name);

        assertEquals(CastStatus.DEPARTED, response.status());
        ArgumentCaptor<FishingTripEntity> captor = ArgumentCaptor.forClass(FishingTripEntity.class);
        verify(fishingTripRepository).save(captor.capture());
        assertFalse(captor.getValue().isCaught());
        assertNull(captor.getValue().getSpeciesId());
        assertEquals(0, captor.getValue().getCatchValue());
    }

    @Test
    void castWhileTripIsOpenDoesNotChargeOrReroll() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 100);
        FishingTripEntity trip = new FishingTripEntity();
        trip.setTwitchName(name);
        trip.setResolvesAt(NOW.plusSeconds(90));

        when(playerRepository.findByTwitchNameForUpdate(name)).thenReturn(Optional.of(entity));
        when(fishingTripRepository.findByTwitchName(name)).thenReturn(Optional.of(trip));

        CastResponse response = fishingService.cast(name);

        assertEquals(CastStatus.WAITING, response.status());
        assertEquals(90, response.remainingSeconds());
        assertEquals(0, response.goldDelta());
        assertEquals(100, response.newBalance());
        verify(fishingRoller, never()).roll();
        verify(fishingTripRepository, never()).save(any());
        verify(playerRepository, never()).save(any());
    }

    @Test
    void castThrowsWhenNotEnoughGold() {
        String name = "broke";
        PlayerEntity entity = new PlayerEntity(1L, name, 2);

        when(playerRepository.findByTwitchNameForUpdate(name)).thenReturn(Optional.of(entity));
        when(fishingTripRepository.findByTwitchName(name)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> fishingService.cast(name));
        verify(fishingRoller, never()).roll();
        verify(fishingTripRepository, never()).save(any());
    }

    @Test
    void claimDuePaysCatchAndClearsTheTrip() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 95);
        Player player = new Player(name, 95);

        FishingTripEntity trip = caughtTrip(name, 15, 1015);

        when(fishingTripRepository.findByResolvesAtLessThanEqualOrderByTwitchNameAsc(NOW))
                .thenReturn(List.of(trip));
        when(playerRepository.findByTwitchNameForUpdate(name)).thenReturn(Optional.of(entity));
        when(playerMapper.toDomain(entity)).thenReturn(player);
        when(fishingTrophyRepository.findByTwitchName(name)).thenReturn(Optional.empty());

        List<BiteResponse> bites = fishingService.claimDue();

        assertEquals(1, bites.size());
        assertTrue(bites.getFirst().caught());
        assertEquals(15, bites.getFirst().goldDelta());
        assertEquals(110, bites.getFirst().newBalance());
        assertTrue(bites.getFirst().newTrophy());
        verify(fishingTrophyRepository).save(any(FishingTrophyEntity.class));
        verify(fishingTripRepository).delete(trip);
    }

    @Test
    void claimDueMissDoesNotPayOrSaveTrophy() {
        String name = "angler";
        PlayerEntity entity = new PlayerEntity(1L, name, 95);
        FishingTripEntity trip = new FishingTripEntity();
        trip.setTwitchName(name);
        trip.setCaught(false);
        trip.setCatchValue(0);
        trip.setScore(0);
        trip.setResolvesAt(NOW.minusSeconds(1));

        when(fishingTripRepository.findByResolvesAtLessThanEqualOrderByTwitchNameAsc(NOW))
                .thenReturn(List.of(trip));
        when(playerRepository.findByTwitchNameForUpdate(name)).thenReturn(Optional.of(entity));

        List<BiteResponse> bites = fishingService.claimDue();

        assertEquals(1, bites.size());
        assertFalse(bites.getFirst().caught());
        assertEquals(0, bites.getFirst().goldDelta());
        assertEquals(95, bites.getFirst().newBalance());
        verify(fishingTrophyRepository, never()).save(any());
        verify(playerRepository, never()).save(any());
        verify(fishingTripRepository).delete(trip);
    }

    private static FishingTripEntity caughtTrip(String name, int catchValue, int score) {
        FishingTripEntity trip = new FishingTripEntity();
        trip.setTwitchName(name);
        trip.setCaught(true);
        trip.setSpeciesId("carp");
        trip.setSpeciesName("Карп");
        trip.setRarity(Rarity.COMMON);
        trip.setCatchValue(catchValue);
        trip.setScore(score);
        trip.setResolvesAt(NOW.minusSeconds(1));
        return trip;
    }
}
