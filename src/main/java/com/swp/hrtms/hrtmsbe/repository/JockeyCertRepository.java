package com.swp.hrtms.hrtmsbe.repository;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.JockeyCert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JockeyCertRepository extends JpaRepository<JockeyCert, Integer> {

    @Query("SELECT jc.certName FROM JockeyCert jc WHERE jc.jockey.id = :jockeyId AND (jc.status = 'PENDING' OR jc.status IS NULL)")
    List<String> findPendingCertificateNamesByJockeyId(@Param("jockeyId") Integer jockeyId);

    @Query("SELECT jc FROM JockeyCert jc WHERE jc.jockey.id = :jockeyId AND (jc.status = 'PENDING' OR jc.status IS NULL)")
    List<JockeyCert> findPendingCertificatesByJockeyId(@Param("jockeyId") Integer jockeyId);

    // Khai: Return all certificates so the jockey can see PENDING, VERIFIED, and REJECTED statuses.
    @Query("SELECT jc FROM JockeyCert jc WHERE jc.jockey.id = :jockeyId ORDER BY jc.id ASC")
    List<JockeyCert> findAllCertificatesByJockeyId(@Param("jockeyId") Integer jockeyId);

    // Khai: Find a certificate only when it belongs to the requested jockey.
    @Query("SELECT jc FROM JockeyCert jc WHERE jc.id = :certId AND jc.jockey.id = :jockeyId")
    Optional<JockeyCert> findCertificateByIdAndJockeyId(
            @Param("certId") Integer certId,
            @Param("jockeyId") Integer jockeyId);
}


