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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final List<String> CERTIFICATE_NOTIFICATION_TYPES = List.of(
            "ACCEPT_CERTIFICATE",
            "REJECT_CERTIFICATE");

    private static final List<String> ADMIN_NOTIFICATION_TYPES = List.of(
            "REFEREE_ACCEPTED",
            "REFEREE_REJECTED",
            "REGISTRATION_VERIFY",
            "VERIFY_CERTIFICATE",
            "DOCTOR_ACCEPTED",
            "DOCTOR_REJECTED");

    private final NotificationRecipientRepository notificationRecipientRepository;
    private final JockeyRepository jockeyRepository;
    private final AdminRepository adminRepository;

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
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getAdminNotifications(Integer adminId, int page, int size, Boolean unreadOnly) {
        if (!adminRepository.existsById(adminId)) {
            throw new ResourceNotFoundException("Admin not found with id: " + adminId);
        }

        Pageable pageable = PageRequest.of(page, size);
        
        if (Boolean.TRUE.equals(unreadOnly)) {
            return notificationRecipientRepository
                    .findByRecipient_IdAndNotification_TypeInAndStatusAndReadAtIsNullOrderByNotification_CreatedAtDesc(
                            adminId,
                            ADMIN_NOTIFICATION_TYPES,
                            "UNREAD",
                            pageable)
                    .map(this::toResponse);
        }

        return notificationRecipientRepository
                .findByRecipient_IdAndNotification_TypeInOrderByNotification_CreatedAtDesc(
                        adminId,
                        ADMIN_NOTIFICATION_TYPES,
                        pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public void markAllAdminNotificationsAsRead(Integer adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new ResourceNotFoundException("Admin not found with id: " + adminId);
        }
        notificationRecipientRepository.markAllAsReadByRecipientId(adminId);
    }

    private NotificationResponse toResponse(com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient) {
        Notification notification = recipient.getNotification();
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .type(notification.getType())
                .status(recipient.getStatus())
                .readAt(recipient.getReadAt())
                .build();
    }
}
