package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.RespondInvitationRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerNotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeInvitationResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import org.springframework.data.domain.Page;

public interface NotificationService {
    List<NotificationResponse> getRecentCertificateNotifications(Integer jockeyId);

    Page<NotificationResponse> getAdminNotifications(Integer adminId, int page, int size, Boolean unreadOnly);

    List<NotificationResponse> markAllAdminNotificationsAsRead(Integer adminId);

    List<NotificationResponse> markNotificationsAsReadByRecipient(Integer recipientId);

    List<HorseOwnerNotificationResponse> getHorseOwnerNotifications(Integer ownerId);

    List<RefereeInvitationResponse> getPendingRefereeInvitations(Integer refereeId);

    RefereeInvitationResponse respondToRefereeInvitation(Integer refereeId, Integer notificationId,
            RespondInvitationRequest request);

    Page<NotificationResponse> getJockeyNotifications(Integer jockeyId, int page, int size);

    List<NotificationResponse> getRefereeNotifications(Integer refereeId);

    Page<NotificationResponse> getSpectatorNotifications(Integer spectatorId, int page, int size);
}
