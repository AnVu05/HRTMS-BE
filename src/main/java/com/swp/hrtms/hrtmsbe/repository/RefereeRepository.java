package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Referee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RefereeRepository extends JpaRepository<Referee, Integer> {

    @Query("SELECT r FROM Referee r WHERE r.id NOT IN " +
           "(SELECT race.referee.id FROM Race race " +
           "WHERE race.referee IS NOT NULL " +
           "AND race.date = :date " +
           "AND race.startTime < :endTime " +
           "AND race.endTime > :startTime " +
           //khai
           "AND race.status != 'CANCELLED')")
    List<Referee> findAvailableReferees(@Param("date") LocalDate date, 
                                        @Param("startTime") LocalTime startTime, 
                                        @Param("endTime") LocalTime endTime);

    @Query("SELECT r FROM Referee r WHERE r.id NOT IN " +
           "(SELECT race.referee.id FROM Race race " +
           "WHERE race.referee IS NOT NULL " +
           "AND race.id != :excludeRaceId " +
           "AND race.date = :date " +
           "AND race.startTime < :endTime " +
           "AND race.endTime > :startTime " +
           //khai
           "AND race.status != 'CANCELLED')")
    List<Referee> findAvailableRefereesExcludingRace(@Param("date") LocalDate date, 
                                                     @Param("startTime") LocalTime startTime, 
                                                     @Param("endTime") LocalTime endTime,
                                                     @Param("excludeRaceId") Integer excludeRaceId);
}

