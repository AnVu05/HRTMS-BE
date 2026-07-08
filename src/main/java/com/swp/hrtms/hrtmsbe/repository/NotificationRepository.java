package com.swp.hrtms.hrtmsbe.repository;

import com.swp.hrtms.hrtmsbe.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @org.springframework.data.jpa.repository.Modifying
    //khai
    @Query("UPDATE Notification n SET n.type = 'DONE' WHERE n.sender.id = :jockeyId AND n.type = 'VERIFI_CERTIFICATE'")
    void updateTypeToDoneVerify(@Param("jockeyId") Integer jockeyId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.type = 'DONE' WHERE n.sender.id = :ownerId AND n.type = 'JOCKEY_INVITATION' AND n.id IN (SELECT nr.notification.id FROM NotificationRecipient nr WHERE nr.recipient.id = :jockeyId)")
    void updateJockeyInvitationToDone(@Param("ownerId") Integer ownerId, @Param("jockeyId") Integer jockeyId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.type = 'DONE' WHERE n.sender.id = :ownerId AND n.type = 'REGISTRATION_VERIFY' AND n.id IN (SELECT nr.notification.id FROM NotificationRecipient nr WHERE nr.recipient.id = :adminId)")
    void updateRegistrationVerifyToDone(@Param("ownerId") Integer ownerId, @Param("adminId") Integer adminId);
    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.type = 'DONE' WHERE n.sender.id = :adminId AND n.type = 'DOCTOR_INVITATION' AND n.id IN (SELECT nr.notification.id FROM NotificationRecipient nr WHERE nr.recipient.id = :doctorId)")
    void updateDoctorInvitationToDone(@Param("adminId") Integer adminId, @Param("doctorId") Integer doctorId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.type = 'DONE' WHERE n.id = :notificationId AND n.type = 'DOCTOR_INVITATION' AND n.id IN (SELECT nr.notification.id FROM NotificationRecipient nr WHERE nr.recipient.id = :doctorId)")
    int updateDoctorInvitationToDoneById(@Param("notificationId") Integer notificationId,
            @Param("doctorId") Integer doctorId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.type = 'DONE' WHERE n.sender.id = :adminId AND n.type = 'DOCTOR_INVITATION' AND n.race.id = :raceId AND n.id IN (SELECT nr.notification.id FROM NotificationRecipient nr WHERE nr.recipient.id = :doctorId)")
    int updateDoctorInvitationToDoneByContext(@Param("adminId") Integer adminId,
            @Param("doctorId") Integer doctorId,
            @Param("raceId") Integer raceId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Notification n SET n.type = 'DONE' WHERE n.sender.id = :adminId AND n.type = 'DOCTOR_INVITATION' AND n.registrationForm.id = :registrationFormId AND n.id IN (SELECT nr.notification.id FROM NotificationRecipient nr WHERE nr.recipient.id = :doctorId)")
    int updateDoctorInvitationToDoneByRegistrationForm(@Param("adminId") Integer adminId,
            @Param("doctorId") Integer doctorId,
            @Param("registrationFormId") Integer registrationFormId);
}
