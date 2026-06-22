package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeInvitationResponse;
import com.swp.hrtms.hrtmsbe.dto.request.RespondInvitationRequest;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getRecentCertificateNotifications(Integer jockeyId);
    List<RefereeInvitationResponse> getPendingRefereeInvitations(Integer refereeId);
    void respondToRefereeInvitation(Integer refereeId, Integer notificationId, RespondInvitationRequest request);
}
