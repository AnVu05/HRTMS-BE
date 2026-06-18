package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.JockeyCert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JockeyCertRepository extends JpaRepository<JockeyCert, Integer> {

    @Query("SELECT jc.certName FROM JockeyCert jc WHERE jc.jockey.id = :jockeyId AND (jc.status = 'PENDING' OR jc.status IS NULL)")
    List<String> findPendingCertificateNamesByJockeyId(@Param("jockeyId") Integer jockeyId);
}
