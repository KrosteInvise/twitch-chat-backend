package com.kroste.twitchchatbackend.fishing;

import com.kroste.twitchchatbackend.players.Player;
import com.kroste.twitchchatbackend.players.PlayerEntity;
import com.kroste.twitchchatbackend.players.PlayerMapper;
import com.kroste.twitchchatbackend.players.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FishingService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final FishingTripRepository fishingTripRepository;
    private final FishingTrophyRepository fishingTrophyRepository;
    private final FishingRoller fishingRoller;
    private final FishingSettings fishingSettings;
    private final Clock clock;

    public CastResponse cast(String twitchName) {
        PlayerEntity playerEntity = requirePlayer(twitchName);
        Optional<FishingTripEntity> existing = fishingTripRepository.findByTwitchName(twitchName);
        if (existing.isPresent()) {
            return waiting(existing.get(), playerEntity.getGold());
        }

        int castCost = fishingSettings.getCastCost();
        if (playerEntity.getGold() < castCost) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Не хватает золота на заброс. Нужно " + castCost + ", баланс: " + playerEntity.getGold()
            );
        }

        Player player = playerMapper.toDomain(playerEntity);
        player.changeBalance(-castCost);
        playerMapper.updateEntityFromDomain(player, playerEntity);
        playerRepository.save(playerEntity);

        int waitMinutes = fishingRoller.rollWaitMinutes(
                fishingSettings.getMinWaitMinutes(),
                fishingSettings.getMaxWaitMinutes()
        );
        Instant resolvesAt = clock.instant().plus(Duration.ofMinutes(waitMinutes));
        CatchRoll roll = fishingRoller.roll();
        fishingTripRepository.save(toTrip(twitchName, roll, resolvesAt));

        return CastResponse.departed(waitMinutes, resolvesAt, castCost, player.getGold());
    }

    public List<BiteResponse> claimDue() {
        List<FishingTripEntity> due = fishingTripRepository
                .findByResolvesAtLessThanEqualOrderByTwitchNameAsc(clock.instant());
        List<BiteResponse> bites = new ArrayList<>();
        for (FishingTripEntity trip : due) {
            BiteResponse bite = settle(trip);
            if (bite != null) {
                bites.add(bite);
            }
        }
        return bites;
    }

    @Transactional(readOnly = true)
    public TrophyResponse getTrophy(String twitchName) {
        FishingTrophyEntity trophy = fishingTrophyRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No trophy for " + twitchName));
        return toTrophyResponse(trophy);
    }

    @Transactional(readOnly = true)
    public List<TrophyResponse> getTop(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 20));
        return fishingTrophyRepository.findAllByOrderByScoreDesc(PageRequest.of(0, safeLimit))
                .stream()
                .map(this::toTrophyResponse)
                .toList();
    }

    private CastResponse waiting(FishingTripEntity trip, int balance) {
        long remaining = Duration.between(clock.instant(), trip.getResolvesAt()).getSeconds();
        if (remaining < 0) {
            remaining = 0;
        }
        return CastResponse.waiting(trip.getResolvesAt(), remaining, balance);
    }

    private BiteResponse settle(FishingTripEntity trip) {
        Optional<PlayerEntity> playerEntity = playerRepository.findByTwitchNameForUpdate(trip.getTwitchName());
        if (playerEntity.isEmpty()) {
            fishingTripRepository.delete(trip);
            return null;
        }

        PlayerEntity entity = playerEntity.get();
        int balance = entity.getGold();
        int goldDelta = 0;
        boolean newTrophy = false;
        if (trip.isCaught()) {
            Player player = playerMapper.toDomain(entity);
            player.changeBalance(trip.getCatchValue());
            playerMapper.updateEntityFromDomain(player, entity);
            playerRepository.save(entity);
            balance = player.getGold();
            goldDelta = trip.getCatchValue();
            newTrophy = upsertTrophyIfBetter(trip);
        }
        fishingTripRepository.delete(trip);
        return new BiteResponse(
                trip.getTwitchName(),
                trip.isCaught(),
                trip.getSpeciesId(),
                trip.getSpeciesName(),
                trip.getRarity(),
                mutationViews(trip),
                trip.getCatchValue(),
                trip.getScore(),
                goldDelta,
                balance,
                newTrophy
        );
    }

    private boolean upsertTrophyIfBetter(FishingTripEntity trip) {
        if (trip.getSpeciesId() == null) {
            return false;
        }

        Optional<FishingTrophyEntity> existing = fishingTrophyRepository.findByTwitchName(trip.getTwitchName());
        if (existing.isPresent() && existing.get().getScore() >= trip.getScore()) {
            return false;
        }

        FishingTrophyEntity trophy = existing.orElseGet(FishingTrophyEntity::new);
        trophy.setTwitchName(trip.getTwitchName());
        trophy.setSpeciesId(trip.getSpeciesId());
        trophy.setSpeciesName(trip.getSpeciesName());
        trophy.setRarity(trip.getRarity());
        trophy.setMutationId(trip.getMutationId());
        trophy.setMutationName(trip.getMutationName());
        trophy.setMutationMultiplier(trip.getMutationMultiplier());
        trophy.setCatchValue(trip.getCatchValue());
        trophy.setScore(trip.getScore());
        trophy.setCaughtAt(clock.instant());
        fishingTrophyRepository.save(trophy);
        return true;
    }

    private FishingTripEntity toTrip(String twitchName, CatchRoll roll, Instant resolvesAt) {
        FishingTripEntity trip = new FishingTripEntity();
        trip.setTwitchName(twitchName);
        trip.setCaught(roll.caught());
        trip.setCatchValue(roll.catchValue());
        trip.setScore(roll.score());
        trip.setResolvesAt(resolvesAt);
        if (!roll.caught()) {
            return trip;
        }

        trip.setSpeciesId(roll.species().id());
        trip.setSpeciesName(roll.species().name());
        trip.setRarity(roll.species().rarity());
        if (roll.mutation() != null) {
            trip.setMutationId(roll.mutation().id());
            trip.setMutationName(roll.mutation().name());
            trip.setMutationMultiplier(roll.mutation().priceMultiplier());
        }
        return trip;
    }

    private List<MutationView> mutationViews(FishingTripEntity trip) {
        MutationView view = MutationView.from(trip.getMutationId(), trip.getMutationName());
        if (view == null) {
            return List.of();
        }
        return List.of(view);
    }

    private List<MutationView> mutationViews(FishingTrophyEntity trophy) {
        MutationView view = MutationView.from(trophy.getMutationId(), trophy.getMutationName());
        if (view == null) {
            return List.of();
        }
        return List.of(view);
    }

    private TrophyResponse toTrophyResponse(FishingTrophyEntity trophy) {
        return new TrophyResponse(
                trophy.getTwitchName(),
                trophy.getSpeciesId(),
                trophy.getSpeciesName(),
                trophy.getRarity(),
                mutationViews(trophy),
                trophy.getCatchValue(),
                trophy.getScore(),
                trophy.getCaughtAt()
        );
    }

    private PlayerEntity requirePlayer(String twitchName) {
        return playerRepository.findByTwitchNameForUpdate(twitchName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Player with name " + twitchName + " does not exist!"
                ));
    }
}
