package com.swp.hrtms.hrtmsbe.repository;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RaceResultRepository extends JpaRepository<RaceResult, Integer> {
    Optional<RaceResult> findByRace_Id(Integer raceId);
}

