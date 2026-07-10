package com.swp.hrtms.hrtmsbe.repository;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.Horse;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorseRepository extends JpaRepository<Horse, Integer> {
    List<Horse> findByOwnerUserId(Integer ownerId);
}


