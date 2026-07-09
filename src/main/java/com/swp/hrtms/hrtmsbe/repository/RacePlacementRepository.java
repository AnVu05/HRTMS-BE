package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.RacePlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RacePlacementRepository extends JpaRepository<RacePlacement, Integer> {
    List<RacePlacement> findByRaceResult_Id(Integer raceResultId);

    @Query("""
            SELECT AVG(rp.finishPosition)
            FROM RacePlacement rp
            WHERE rp.registrationForm.jockey.id = :jockeyId
            AND rp.registrationForm.status = :status
            AND rp.finishPosition IS NOT NULL
            """)
    Double findAverageFinishPositionByJockeyIdAndRegistrationStatus(
            @Param("jockeyId") Integer jockeyId,
            @Param("status") com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status);
}


