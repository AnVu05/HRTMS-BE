package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Integer> {

    List<NotificationRecipient> findTop3ByRecipient_IdAndNotification_TypeInOrderByNotification_CreatedAtDesc(
            Integer recipientId,
            Collection<String> types);

    Page<NotificationRecipient> findByRecipient_IdAndNotification_TypeInOrderByNotification_CreatedAtDesc(
            Integer recipientId,
            Collection<String> types,
            Pageable pageable);

    Page<NotificationRecipient> findByRecipient_IdAndNotification_TypeInAndStatusAndReadAtIsNullOrderByNotification_CreatedAtDesc(
            Integer recipientId,
            Collection<String> types,
            String status,
            Pageable pageable);

    @Query("SELECT nr FROM NotificationRecipient nr WHERE nr.recipient.id = :recipientId AND nr.status = 'None' AND nr.notification.type = 'VERIFY_CERTIFICATE'")
    List<NotificationRecipient> findPendingVerificationRequests(@Param("recipientId") Integer recipientId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE NotificationRecipient nr SET nr.status = 'Accept' WHERE nr.recipient.id = :adminId AND nr.status = 'None' AND nr.notification.type = 'VERIFY_CERTIFICATE' AND nr.notification.sender.id = :jockeyId")
    void markVerificationRequestAsAccepted(@Param("adminId") Integer adminId, @Param("jockeyId") Integer jockeyId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE NotificationRecipient nr SET nr.status = 'Reject' WHERE nr.recipient.id = :adminId AND nr.status = 'None' AND nr.notification.type = 'VERIFY_CERTIFICATE' AND nr.notification.sender.id = :jockeyId")
    void markVerificationRequestAsRejected(@Param("adminId") Integer adminId, @Param("jockeyId") Integer jockeyId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE NotificationRecipient nr SET nr.status = 'READ', nr.readAt = CURRENT_TIMESTAMP WHERE nr.recipient.id = :adminId AND nr.status = 'UNREAD'")
    void markAllAsReadByRecipientId(@Param("adminId") Integer adminId);
}
