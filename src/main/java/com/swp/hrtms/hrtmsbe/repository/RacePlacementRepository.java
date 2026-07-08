package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.RacePlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RacePlacementRepository extends JpaRepository<RacePlacement, Integer> {
    List<RacePlacement> findByRaceResult_Id(Integer raceResultId);
}


