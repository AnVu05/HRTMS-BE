package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;

import java.util.List;
import org.springframework.data.domain.Page;

public interface NotificationService {
    List<NotificationResponse> getRecentCertificateNotifications(Integer jockeyId);
    Page<NotificationResponse> getAdminNotifications(Integer adminId, int page, int size, Boolean unreadOnly);
    void markAllAdminNotificationsAsRead(Integer adminId);
}
