package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Integer> {
    boolean existsBySpectator_IdAndRace_Id(Integer spectatorId, Integer raceId);

    List<Prediction> findByRace_Id(Integer raceId);

    List<Prediction> findByRace_Tournament_Id(Integer tournamentId);
}
