package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Race;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface RaceRepository extends JpaRepository<Race, Integer> {

    @Query("SELECT COUNT(r) > 0 FROM Race r WHERE r.tournament.id = :tournamentId AND r.date = :date AND r.startTime < :endTime AND r.endTime > :startTime")
    boolean existsOverlappingInTournament(@Param("tournamentId") Integer tournamentId, 
                                          @Param("date") LocalDate date, 
                                          @Param("startTime") LocalTime startTime, 
                                          @Param("endTime") LocalTime endTime);

    @Query("SELECT COUNT(r) > 0 FROM Race r WHERE r.referee.id = :refereeId AND r.date = :date AND r.startTime < :endTime AND r.endTime > :startTime")
    boolean existsOverlappingForReferee(@Param("refereeId") Integer refereeId, 
                                        @Param("date") LocalDate date, 
                                        @Param("startTime") LocalTime startTime, 
                                        @Param("endTime") LocalTime endTime);
}
