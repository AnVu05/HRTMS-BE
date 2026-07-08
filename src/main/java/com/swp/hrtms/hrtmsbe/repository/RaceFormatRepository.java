package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.RaceFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceFormatRepository extends JpaRepository<RaceFormat, Integer> {
}
