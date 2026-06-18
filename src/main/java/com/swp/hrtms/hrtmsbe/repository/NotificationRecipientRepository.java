package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Integer> {

    @Query("SELECT nr FROM NotificationRecipient nr WHERE nr.recipient.id = :recipientId AND nr.status = 'None' AND nr.notification.type = 'VERIFY_CERTIFICATE'")
    List<NotificationRecipient> findPendingVerificationRequests(@Param("recipientId") Integer recipientId);
}
