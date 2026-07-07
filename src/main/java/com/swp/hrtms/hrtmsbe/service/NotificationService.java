package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerNotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeInvitationResponse;
import com.swp.hrtms.hrtmsbe.dto.request.RespondInvitationRequest;

import java.util.List;
import org.springframework.data.domain.Page;

public interface NotificationService {
    List<NotificationResponse> getRecentCertificateNotifications(Integer jockeyId);

    Page<NotificationResponse> getAdminNotifications(Integer adminId, int page, int size, Boolean unreadOnly);

    void markAllAdminNotificationsAsRead(Integer adminId);

    Page<HorseOwnerNotificationResponse> getHorseOwnerNotifications(Integer ownerId, int page, int size);

    Page<NotificationResponse> getJockeyNotifications(Integer jockeyId, int page, int size);

    List<RefereeInvitationResponse> getPendingRefereeInvitations(Integer refereeId);

    void respondToRefereeInvitation(Integer refereeId, Integer notificationId, RespondInvitationRequest request);

    Page<NotificationResponse> getSpectatorNotifications(Integer spectatorId, int page, int size);
}
