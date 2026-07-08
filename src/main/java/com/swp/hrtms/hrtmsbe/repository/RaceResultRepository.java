package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.RaceResult;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RaceResultRepository extends JpaRepository<RaceResult, Integer> {
    Optional<RaceResult> findByRace_Id(Integer raceId);
}


