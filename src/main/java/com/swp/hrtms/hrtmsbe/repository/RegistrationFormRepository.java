package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface RegistrationFormRepository extends JpaRepository<RegistrationForm, Integer> {
        List<RegistrationForm> findByStatusAndCreatedAtBefore(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status,
                        LocalDateTime cutoff);

        List<RegistrationForm> findByRace_IdAndStatus(Integer raceId,
                        com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status);

        List<RegistrationForm> findByOwner_UserId(Integer ownerId);

        List<RegistrationForm> findByAdmin_IdAndStatus(Integer adminId, com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status);

        List<RegistrationForm> findByRace_Id(Integer raceId);

        long countByRace_IdAndStatusNotIn(Integer raceId,
                        List<com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus> statuses);

        boolean existsByHorse_IdAndRace_IdAndStatusNotIn(Integer horseId, Integer raceId,
                        List<com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus> statuses);

        boolean existsByJockey_IdAndRace_IdAndStatusNotIn(Integer jockeyId, Integer raceId,
                        List<com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus> statuses);

        @Query("""
                        SELECT COUNT(DISTINCT rf.race.id)
                        FROM RegistrationForm rf
                        WHERE rf.jockey.id = :jockeyId
                        AND rf.status = :status
                        AND rf.race IS NOT NULL
                        """)
        long countDistinctRacesByJockeyIdAndStatus(
                        @Param("jockeyId") Integer jockeyId,
                        @Param("status") com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status);
}
