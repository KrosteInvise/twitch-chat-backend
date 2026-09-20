package com.kroste.twitchchatbackend.fishing;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FishingTrophyRepository extends JpaRepository<FishingTrophyEntity, Long> {

    Optional<FishingTrophyEntity> findByTwitchName(String twitchName);

    List<FishingTrophyEntity> findAllByOrderByScoreDesc(Pageable pageable);
}
