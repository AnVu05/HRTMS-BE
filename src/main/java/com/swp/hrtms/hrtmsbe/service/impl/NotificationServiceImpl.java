package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerNotificationResponse;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
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

    private static final List<String> HORSE_OWNER_ADMIN_NOTIFICATION_TYPES = List.of(
            "NEW_TOURNAMENT",
            "TOURNAMENT_UPDATE",
            "TOURNAMENT_CANCELLED",

            "NEW_RACE",
            "RACE_UPDATE",
            "RACE_CANCELLED",
            
            "REGISTRATION_APPROVED",
            "REGISTRATION_REJECTED");

    private static final List<String> HORSE_OWNER_JOCKEY_NOTIFICATION_TYPES = List.of(
            "INVITATION_ACCEPTED",
            "INVITATION_REJECTED");

    private final NotificationRecipientRepository notificationRecipientRepository;
    private final JockeyRepository jockeyRepository;
    private final HorseOwnerRepository horseOwnerRepository;

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

    @Override
    @Transactional(readOnly = true)
    public List<HorseOwnerNotificationResponse> getHorseOwnerNotifications(Integer ownerId) {
        if (!horseOwnerRepository.existsById(ownerId)) {
            throw new ResourceNotFoundException("Horse owner not found with id: " + ownerId);
        }

        return notificationRecipientRepository
                .findHorseOwnerNotifications(
                        ownerId,
                        HORSE_OWNER_ADMIN_NOTIFICATION_TYPES,
                        HORSE_OWNER_JOCKEY_NOTIFICATION_TYPES)
                .stream()
                .map(this::toHorseOwnerResponse)
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

    private HorseOwnerNotificationResponse toHorseOwnerResponse(NotificationRecipient recipient) {
        Notification notification = recipient.getNotification();

        return HorseOwnerNotificationResponse.builder()
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .createdAt(notification.getCreatedAt())
                .readAt(recipient.getReadAt())
                .build();
    }
}
