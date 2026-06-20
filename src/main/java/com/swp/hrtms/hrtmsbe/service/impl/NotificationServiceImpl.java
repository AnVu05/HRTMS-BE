package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.JockeyRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final List<String> CERTIFICATE_NOTIFICATION_TYPES = List.of(
            "ACCEPT_CERTIFICATE",
            "REJECT_CERTIFICATE");

    private final NotificationRecipientRepository notificationRecipientRepository;
    private final JockeyRepository jockeyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getRecentCertificateNotifications(Integer jockeyId) {
        if (!jockeyRepository.existsById(jockeyId)) {
            throw new ResourceNotFoundException("Jockey not found with id: " + jockeyId);
        }

        return notificationRecipientRepository
                .findTop3ByRecipient_IdAndNotification_TypeInOrderByNotification_CreatedAtDesc(
                        jockeyId,
                        CERTIFICATE_NOTIFICATION_TYPES)
                .stream()
                .map(recipient -> toResponse(recipient.getNotification()))
                .toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .type(notification.getType())
                .build();
    }
}
