package com.swp.hrtms.hrtmsbe.repository;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp.hrtms.hrtmsbe.enums.UserStatus;
import java.util.List;

@Repository
public interface JockeyRepository extends JpaRepository<Jockey, Integer> {
    List<Jockey> findByStatus(UserStatus status);
}


