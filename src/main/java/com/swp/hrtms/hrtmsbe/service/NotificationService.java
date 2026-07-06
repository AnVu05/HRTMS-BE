package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerNotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeInvitationResponse;
import com.swp.hrtms.hrtmsbe.dto.request.RespondInvitationRequest;

import java.util.List;
import org.springframework.data.domain.Page;

public interface NotificationService {
    List<NotificationResponse> getRecentCertificateNotifications(Integer jockeyId);
    Page<NotificationResponse> getAdminNotifications(Integer adminId, int page, int size, Boolean unreadOnly);
    List<NotificationResponse> markAllAdminNotificationsAsRead(Integer adminId);
    List<NotificationResponse> markNotificationsAsReadByRecipient(Integer recipientId);

    List<HorseOwnerNotificationResponse> getHorseOwnerNotifications(Integer ownerId);
    List<RefereeInvitationResponse> getPendingRefereeInvitations(Integer refereeId);
    RefereeInvitationResponse respondToRefereeInvitation(Integer refereeId, Integer notificationId, RespondInvitationRequest request);
}


