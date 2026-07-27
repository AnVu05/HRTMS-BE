package com.swp.hrtms.hrtmsbe.repository;

// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.List;
import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Integer> {

  List<NotificationRecipient> findTop3ByRecipient_IdAndNotification_TypeInOrderByNotification_CreatedAtDesc(
      Integer recipientId,
      Collection<com.swp.hrtms.hrtmsbe.enums.NotificationType> types);

  List<NotificationRecipient> findByRecipient_IdAndNotification_TypeOrderByNotification_CreatedAtDesc(
      Integer recipientId,
      com.swp.hrtms.hrtmsbe.enums.NotificationType type);

  // khai
  @Query("SELECT nr FROM NotificationRecipient nr WHERE nr.recipient.id = :recipientId AND nr.status = 'UNREAD' AND nr.notification.type = 'VERIFI_CERTIFICATE'")
  List<NotificationRecipient> findPendingVerificationRequests(@Param("recipientId") Integer recipientId);

  @Query("""
      SELECT nr
      FROM NotificationRecipient nr
      JOIN FETCH nr.notification n
      JOIN FETCH n.sender sender
      WHERE nr.recipient.id = :ownerId
        AND (
              (sender.role = 'ADMIN' AND n.type IN :adminTypes)
              OR
              (sender.role = 'JOCKEY' AND n.type IN :jockeyTypes)
            )
      ORDER BY n.createdAt DESC
      """)
  List<NotificationRecipient> findHorseOwnerNotifications(
      @Param("ownerId") Integer ownerId,
      @Param("adminTypes") Collection<com.swp.hrtms.hrtmsbe.enums.NotificationType> adminTypes,
      @Param("jockeyTypes") Collection<com.swp.hrtms.hrtmsbe.enums.NotificationType> jockeyTypes);

  @org.springframework.data.jpa.repository.Modifying
  @Query("UPDATE NotificationRecipient nr SET nr.status = 'READ', nr.readAt = CURRENT_TIMESTAMP WHERE nr.recipient.id = :recipientId AND nr.status = 'UNREAD'")
  void markAllAsReadByRecipientId(@Param("recipientId") Integer recipientId);

  @org.springframework.data.jpa.repository.Modifying
  @Query("UPDATE NotificationRecipient nr SET nr.status = 'Accept' WHERE nr.recipient.id = :adminId AND nr.status = 'None' AND nr.notification.type = 'VERIFY_CERTIFICATE' AND nr.notification.sender.id = :jockeyId")
  void markVerificationRequestAsAccepted(@Param("adminId") Integer adminId, @Param("jockeyId") Integer jockeyId);

  @org.springframework.data.jpa.repository.Modifying
  @Query("UPDATE NotificationRecipient nr SET nr.status = 'Reject' WHERE nr.recipient.id = :adminId AND nr.status = 'None' AND nr.notification.type = 'VERIFY_CERTIFICATE' AND nr.notification.sender.id = :jockeyId")
  void markVerificationRequestAsRejected(@Param("adminId") Integer adminId, @Param("jockeyId") Integer jockeyId);

  @org.springframework.data.jpa.repository.Modifying
  @Query("UPDATE NotificationRecipient nr SET nr.status = 'DONE_VERIFY' WHERE nr.status = 'None' AND nr.notification.type = 'VERIFY_CERTIFICATE' AND nr.notification.sender.id = :jockeyId")
  void markAllOtherVerificationRequestsAsDoneVerify(@Param("jockeyId") Integer jockeyId);

  @org.springframework.data.jpa.repository.Modifying
  @Query("UPDATE NotificationRecipient nr SET nr.status = 'READ', nr.readAt = CURRENT_TIMESTAMP WHERE nr.recipient.id = :jockeyId AND nr.status = 'UNREAD' AND nr.notification.id IN (SELECT n.id FROM Notification n WHERE n.sender.id = :ownerId AND n.type = 'JOCKEY_INVITATION')")
  void markJockeyInvitationAsRead(@Param("ownerId") Integer ownerId, @Param("jockeyId") Integer jockeyId);

  @org.springframework.data.jpa.repository.Modifying
  @Query("UPDATE NotificationRecipient nr SET nr.status = 'READ', nr.readAt = CURRENT_TIMESTAMP WHERE nr.recipient.id = :adminId AND nr.status = 'UNREAD' AND nr.notification.id IN (SELECT n.id FROM Notification n WHERE n.sender.id = :ownerId AND n.type = 'REGISTRATION_VERIFY')")
  void markRegistrationVerifyAsRead(@Param("ownerId") Integer ownerId, @Param("adminId") Integer adminId);

  Page<NotificationRecipient> findByRecipient_IdAndNotification_TypeInOrderByNotification_CreatedAtDesc(
      Integer recipientId,
      Collection<NotificationType> types,
      Pageable pageable);

  Page<NotificationRecipient> findByRecipient_IdAndNotification_TypeInAndStatusAndReadAtIsNullOrderByNotification_CreatedAtDesc(
      Integer recipientId,
      Collection<NotificationType> types,
      com.swp.hrtms.hrtmsbe.enums.NotificationStatus status,
      Pageable pageable);

  List<NotificationRecipient> findByRecipient_IdAndNotification_TypeInAndStatusAndReadAtIsNullOrderByNotification_CreatedAtDesc(
      Integer recipientId,
      Collection<NotificationType> types,
      com.swp.hrtms.hrtmsbe.enums.NotificationStatus status);

  List<NotificationRecipient> findByRecipient_IdAndNotification_TypeInAndStatusOrderByNotification_CreatedAtDesc(
      Integer recipientId,
      Collection<NotificationType> types,
      com.swp.hrtms.hrtmsbe.enums.NotificationStatus status);

  @Query(value = """
      SELECT nr
      FROM NotificationRecipient nr
      JOIN FETCH nr.notification n
      LEFT JOIN FETCH n.sender sender
      WHERE nr.recipient.id = :ownerId
        AND (
              (sender IS NULL AND n.type IN :adminTypes)
              OR
              (sender.role = 'ADMIN' AND n.type IN :adminTypes)
              OR
              (sender.role = 'JOCKEY' AND n.type IN :jockeyTypes)
              OR
              (sender.role = 'DOCTOR' AND n.type IN :doctorTypes)
            )
      ORDER BY n.createdAt DESC
      """, countQuery = """
      SELECT count(nr)
      FROM NotificationRecipient nr
      JOIN nr.notification n
      LEFT JOIN n.sender sender
      WHERE nr.recipient.id = :ownerId
        AND (
              (sender IS NULL AND n.type IN :adminTypes)
              OR
              (sender.role = 'ADMIN' AND n.type IN :adminTypes)
              OR
              (sender.role = 'JOCKEY' AND n.type IN :jockeyTypes)
              OR
              (sender.role = 'DOCTOR' AND n.type IN :doctorTypes)
            )
      """)
  Page<NotificationRecipient> findHorseOwnerNotifications(
      @Param("ownerId") Integer ownerId,
      @Param("adminTypes") Collection<NotificationType> adminTypes,
      @Param("jockeyTypes") Collection<NotificationType> jockeyTypes,
      @Param("doctorTypes") Collection<NotificationType> doctorTypes,
      Pageable pageable);

  // Lấy lời mời trọng tài đang chờ: Trạng thái thông báo nhận là 'None' (chưa
  // đọc),
  // cuộc đua tương ứng vẫn đang ở trạng thái 'PENDING_REFEREE' và trọng tài được
  // phân công trùng khớp.
  @Query("""
      SELECT nr
      FROM NotificationRecipient nr
      JOIN nr.notification n
      JOIN n.race r
      WHERE nr.recipient.id = :refereeId
        AND nr.status = 'UNREAD'
        AND n.type = 'REFEREE_INVITATION'
        AND r.referee.id = :refereeId
        AND r.status = 'PENDING_REFEREE'
      """)
  List<NotificationRecipient> findPendingRefereeInvitations(@Param("refereeId") Integer refereeId);

  @Query("""
      SELECT nr
      FROM NotificationRecipient nr
      JOIN FETCH nr.notification n
      JOIN FETCH n.race r
      JOIN FETCH r.tournament t
      WHERE nr.status = 'UNREAD'
        AND (n.type = 'REFEREE_INVITATION' OR n.type = 'SYSTEM')
        AND r.referee.id = nr.recipient.id
        AND r.status = 'PENDING_REFEREE'
        AND n.createdAt < :cutoff
      """)
  List<NotificationRecipient> findExpiredRefereeInvitations(@Param("cutoff") java.time.LocalDateTime cutoff);

  java.util.Optional<NotificationRecipient> findByNotification_IdAndRecipient_Id(Integer notificationId,
      Integer recipientId);

  java.util.Optional<NotificationRecipient> findByIdAndRecipient_Id(Integer id, Integer recipientId);

  List<NotificationRecipient> findByRecipient_IdOrderByNotification_CreatedAtDesc(Integer recipientId);
}
