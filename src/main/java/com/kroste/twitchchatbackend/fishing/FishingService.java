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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FishingService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;
    private final PendingCatchRepository pendingCatchRepository;
    private final FishingTrophyRepository fishingTrophyRepository;
    private final FishingRoller fishingRoller;
    private final FishCatalog fishCatalog;

    public CastResponse cast(String twitchName) {
        PlayerEntity playerEntity = requirePlayer(twitchName);
        Player player = playerMapper.toDomain(playerEntity);

        KeepSnapshot autoKept = autoResolveExpiredOrExistingBeforeCast(twitchName, player, playerEntity);

        if (player.getGold() < FishingProperties.CAST_COST) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Not enough gold for cast! Need " + FishingProperties.CAST_COST + ", balance: " + player.getGold()
            );
        }

        player.changeBalance(-FishingProperties.CAST_COST);
        playerMapper.updateEntityFromDomain(player, playerEntity);
        playerRepository.save(playerEntity);

        CatchRoll roll = fishingRoller.roll();
        if (!roll.caught()) {
            return CastResponse.miss(FishingProperties.CAST_COST, player.getGold(), autoKept);
        }

        PendingCatchEntity pending = toPendingEntity(twitchName, roll);
        pendingCatchRepository.save(pending);

        return toCastResponse(pending, player.getGold(), autoKept);
    }

    public KeepResponse keep(String twitchName) {
        PlayerEntity playerEntity = requirePlayer(twitchName);
        Player player = playerMapper.toDomain(playerEntity);

        PendingCatchEntity pending = pendingCatchRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No pending catch to keep"));

        KeepSnapshot snapshot = settleKeep(pending, player, playerEntity);
        return new KeepResponse(
                true,
                snapshot.speciesId(),
                snapshot.speciesName(),
                snapshot.rarity(),
                snapshot.mutations(),
                snapshot.catchValue(),
                snapshot.score(),
                snapshot.goldDelta(),
                snapshot.newBalance(),
                snapshot.newTrophy()
        );
    }

    public RerollResponse reroll(String twitchName) {
        PlayerEntity playerEntity = requirePlayer(twitchName);
        Player player = playerMapper.toDomain(playerEntity);

        PendingCatchEntity pending = pendingCatchRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No pending catch"));

        if (isExpired(pending)) {
            settleKeep(pending, player, playerEntity);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pending catch expired and was auto-kept");
        }

        if (pending.getRerollsUsed() >= FishingProperties.MAX_REROLLS) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Reroll limit reached. Use keep.");
        }

        if (player.getGold() < FishingProperties.REROLL_COST) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Not enough gold for reroll! Need " + FishingProperties.REROLL_COST + ", balance: " + player.getGold()
            );
        }

        player.changeBalance(-FishingProperties.REROLL_COST);
        playerMapper.updateEntityFromDomain(player, playerEntity);
        playerRepository.save(playerEntity);

        pending.setRerollsUsed(pending.getRerollsUsed() + 1);

        RerollOutcome outcome = fishingRoller.rollRerollOutcome();
        if (outcome == RerollOutcome.PUFF) {
            pendingCatchRepository.delete(pending);
            return new RerollResponse(
                    RerollOutcome.PUFF,
                    true,
                    false,
                    null,
                    null,
                    null,
                    List.of(),
                    null,
                    null,
                    FishingProperties.REROLL_COST,
                    -FishingProperties.REROLL_COST,
                    player.getGold(),
                    null
            );
        }

        if (outcome == RerollOutcome.STRIP) {
            if (pending.getMutationId() == null) {
                outcome = RerollOutcome.SAME;
            } else {
                applyMutation(pending, null);
            }
        } else if (outcome == RerollOutcome.UPGRADE) {
            Mutation next = fishingRoller.pickMutationDifferentFrom(pending.getMutationId());
            applyMutation(pending, next);
        }

        pendingCatchRepository.save(pending);
        int remaining = FishingProperties.MAX_REROLLS - pending.getRerollsUsed();

        return new RerollResponse(
                outcome,
                false,
                true,
                pending.getSpeciesId(),
                pending.getSpeciesName(),
                pending.getRarity(),
                mutationViews(pending),
                pending.getCatchValue(),
                pending.getScore(),
                FishingProperties.REROLL_COST,
                -FishingProperties.REROLL_COST,
                player.getGold(),
                remaining
        );
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

    private KeepSnapshot autoResolveExpiredOrExistingBeforeCast(String twitchName, Player player, PlayerEntity playerEntity) {
        Optional<PendingCatchEntity> existing = pendingCatchRepository.findByTwitchName(twitchName);
        if (existing.isEmpty()) {
            return null;
        }
        // Any existing pending is auto-kept before a new cast (including expired).
        return settleKeep(existing.get(), player, playerEntity);
    }

    private KeepSnapshot settleKeep(PendingCatchEntity pending, Player player, PlayerEntity playerEntity) {
        player.changeBalance(pending.getCatchValue());
        playerMapper.updateEntityFromDomain(player, playerEntity);
        playerRepository.save(playerEntity);

        boolean newTrophy = upsertTrophyIfBetter(pending);
        pendingCatchRepository.delete(pending);

        return new KeepSnapshot(
                pending.getSpeciesId(),
                pending.getSpeciesName(),
                pending.getRarity(),
                mutationViews(pending),
                pending.getCatchValue(),
                pending.getScore(),
                newTrophy,
                pending.getCatchValue(),
                player.getGold()
        );
    }

    private boolean upsertTrophyIfBetter(PendingCatchEntity pending) {
        Optional<FishingTrophyEntity> existing = fishingTrophyRepository.findByTwitchName(pending.getTwitchName());
        if (existing.isPresent() && existing.get().getScore() >= pending.getScore()) {
            return false;
        }

        FishingTrophyEntity trophy = existing.orElseGet(FishingTrophyEntity::new);
        trophy.setTwitchName(pending.getTwitchName());
        trophy.setSpeciesId(pending.getSpeciesId());
        trophy.setSpeciesName(pending.getSpeciesName());
        trophy.setRarity(pending.getRarity());
        trophy.setMutationId(pending.getMutationId());
        trophy.setMutationName(pending.getMutationName());
        trophy.setMutationMultiplier(pending.getMutationMultiplier());
        trophy.setCatchValue(pending.getCatchValue());
        trophy.setScore(pending.getScore());
        trophy.setCaughtAt(Instant.now());
        fishingTrophyRepository.save(trophy);
        return true;
    }

    private PendingCatchEntity toPendingEntity(String twitchName, CatchRoll roll) {
        PendingCatchEntity pending = new PendingCatchEntity();
        pending.setTwitchName(twitchName);
        pending.setSpeciesId(roll.species().id());
        pending.setSpeciesName(roll.species().name());
        pending.setRarity(roll.species().rarity());
        applyMutation(pending, roll.mutation());
        pending.setRerollsUsed(0);
        pending.setExpiresAt(Instant.now().plusSeconds(FishingProperties.PENDING_TTL_SECONDS));
        return pending;
    }

    private void applyMutation(PendingCatchEntity pending, Mutation mutation) {
        FishSpecies species = fishCatalog.findSpeciesById(pending.getSpeciesId())
                .orElseThrow(() -> new IllegalStateException("Unknown species " + pending.getSpeciesId()));

        if (mutation == null) {
            pending.setMutationId(null);
            pending.setMutationName(null);
            pending.setMutationMultiplier(null);
            CatchRoll valued = CatchRoll.of(species, null);
            pending.setCatchValue(valued.catchValue());
            pending.setScore(valued.score());
            return;
        }

        pending.setMutationId(mutation.id());
        pending.setMutationName(mutation.name());
        pending.setMutationMultiplier(mutation.priceMultiplier());
        CatchRoll valued = CatchRoll.of(species, mutation);
        pending.setCatchValue(valued.catchValue());
        pending.setScore(valued.score());
    }

    private CastResponse toCastResponse(PendingCatchEntity pending, int newBalance, KeepSnapshot autoKept) {
        return new CastResponse(
                true,
                true,
                pending.getSpeciesId(),
                pending.getSpeciesName(),
                pending.getRarity(),
                mutationViews(pending),
                pending.getCatchValue(),
                pending.getScore(),
                FishingProperties.CAST_COST,
                -FishingProperties.CAST_COST,
                newBalance,
                FishingProperties.MAX_REROLLS - pending.getRerollsUsed(),
                FishingProperties.REROLL_COST,
                pending.getExpiresAt(),
                autoKept
        );
    }

    private List<MutationView> mutationViews(PendingCatchEntity pending) {
        MutationView view = MutationView.from(pending.getMutationId(), pending.getMutationName());
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

    private boolean isExpired(PendingCatchEntity pending) {
        return pending.getExpiresAt().isBefore(Instant.now());
    }

    private PlayerEntity requirePlayer(String twitchName) {
        return playerRepository.findByTwitchName(twitchName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Player with name " + twitchName + " does not exist!"
                ));
    }
}
