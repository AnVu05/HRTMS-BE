package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Integer> {
    java.util.List<HealthCheck> findByStatusAndCheckDateBefore(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus status, java.time.LocalDateTime cutoff);

    boolean existsByRegistrationForm_Id(Integer registrationFormId);
    
    java.util.List<HealthCheck> findByDoctor_UserId(Integer doctorId);
}

