package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.type = 'DONE_VERIFY' WHERE n.sender.id = :jockeyId AND n.type = 'VERIFY_CERTIFICATE'")
    void updateTypeToDoneVerify(@Param("jockeyId") Integer jockeyId);
}
