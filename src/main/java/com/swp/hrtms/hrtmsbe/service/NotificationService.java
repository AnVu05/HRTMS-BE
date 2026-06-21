package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getRecentCertificateNotifications(Integer jockeyId);
}
