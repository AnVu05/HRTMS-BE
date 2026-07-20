package com.swp.hrtms.hrtmsbe.repository;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    @Query("""
            SELECT a
            FROM Admin a
            ORDER BY (
                SELECT COUNT(rf) FROM RegistrationForm rf WHERE rf.admin = a AND rf.status = com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_ADMIN
            ) ASC
            """)
    Page<Admin> findLeastLoadedAdmin(Pageable pageable);
}


