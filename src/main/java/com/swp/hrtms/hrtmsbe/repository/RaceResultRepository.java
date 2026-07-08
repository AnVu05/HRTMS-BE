package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RaceResultRepository extends JpaRepository<RaceResult, Integer> {
    Optional<RaceResult> findByRace_Id(Integer raceId);
}

