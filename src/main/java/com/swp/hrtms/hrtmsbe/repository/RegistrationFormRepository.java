package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RegistrationFormRepository extends JpaRepository<RegistrationForm, Integer> {
    List<RegistrationForm> findByStatusAndCreatedAtBefore(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status, LocalDateTime cutoff);
    List<RegistrationForm> findByRace_IdAndStatus(Integer raceId, com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status);
    List<RegistrationForm> findByRace_Id(Integer raceId);
}



