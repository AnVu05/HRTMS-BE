package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query("SELECT COUNT(t) > 0 FROM Tournament t WHERE t.startDate <= :endDate AND t.endDate >= :startDate AND t.status != 'CANCELED'")
    boolean existsOverlappingTournament(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
