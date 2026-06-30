package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Integer> {
    boolean existsBySpectatorIdAndRaceId(Integer spectatorId, Integer raceId);

    List<Prediction> findByRaceId(Integer raceId);
}
