package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Horse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorseRepository extends JpaRepository<Horse, Integer> {

    //Khai
    List<Horse> findByOwnerUserId(Integer ownerId);
}
