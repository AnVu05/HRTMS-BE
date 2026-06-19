package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;
import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.entity.JockeyCert;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.entity.UserRole;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private static final String NOTIFICATION_TITLE = "Certificate Verification Request";
    private static final String NOTIFICATION_CONTENT =
            "A jockey has requested verification for all pending certificates.";
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final JockeyCertRepository jockeyCertRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JockeyVerificationRequestResponse> getJockeyVerificationRequests(Integer recipientId) {
        List<NotificationRecipient> recipients = notificationRecipientRepository
                .findPendingVerificationRequests(recipientId);
        List<JockeyVerificationRequestResponse> responses = new ArrayList<>();

        for (NotificationRecipient nr : recipients) {
            org.hibernate.Hibernate.initialize(nr.getNotification().getSender());
            Object unproxiedSender = org.hibernate.Hibernate.unproxy(nr.getNotification().getSender());
            if (unproxiedSender instanceof Jockey) {
                Jockey jockey = (Jockey) unproxiedSender;
                List<String> pendingCerts = jockeyCertRepository.findPendingCertificateNamesByJockeyId(jockey.getId());

                if (!pendingCerts.isEmpty()) {
                    JockeyVerificationRequestResponse response = JockeyVerificationRequestResponse.builder()
                            .notificationId(nr.getNotification().getId())
                            .jockeyId(jockey.getId())
                            .jockeyName(jockey.getJockeyName())
                            .pendingCertificates(pendingCerts)
                            .build();
                    responses.add(response);
                }
            }
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse> getPendingCertificateImages(
            Integer jockeyId) {
        List<com.swp.hrtms.hrtmsbe.entity.JockeyCert> certs = jockeyCertRepository
                .findPendingCertificatesByJockeyId(jockeyId);
        List<com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse> responses = new ArrayList<>();

        for (com.swp.hrtms.hrtmsbe.entity.JockeyCert cert : certs) {
            String base64Image = null;
            if (cert.getCertImg() != null) {
                base64Image = cert.getCertImg();
            }

            responses.add(com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse.builder()
                    .certImageBase64(base64Image)
                    .build());
        }

        return responses;
    }

    @Override
    @Transactional
    // Khai: Store the frontend Base64 value directly as text.
    public Integer createJockeyCertificate(Integer jockeyId, JockeyCertCreateRequest request) {
        Jockey jockey = findJockeyById(jockeyId);

        if (request == null || request.getCertName() == null || request.getCertName().isBlank()) {
            throw new IllegalArgumentException("Certificate name cannot be empty");
        }
        if (request.getCertImageBase64() == null || request.getCertImageBase64().isBlank()) {
            throw new IllegalArgumentException("Certificate image cannot be empty");
        }

        JockeyCert certificate = JockeyCert.builder()
                .certName(request.getCertName().trim())
                .certImg(request.getCertImageBase64())
                .jockey(jockey)
                .build();

        return jockeyCertRepository.save(certificate).getId();
    }

    @Override
    @Transactional
    // Khai: Create a notification from the jockey and one unread recipient row per admin.
    public Integer requestVerificationForAll(Integer jockeyId) {
        Jockey jockey = findJockeyById(jockeyId);
        if (jockeyCertRepository.findPendingCertificatesByJockeyId(jockeyId).isEmpty()) {
            throw new IllegalArgumentException("No pending certificates found for this jockey");
        }

        List<Admin> admins = adminRepository.findAll();
        if (admins.isEmpty()) {
            throw new IllegalArgumentException("No admin account is available to receive the request");
        }

        Notification notification = Notification.builder()
                .sender(jockey)
                .title(NOTIFICATION_TITLE)
                .content(NOTIFICATION_CONTENT)
                .build();
        Notification savedNotification = notificationRepository.save(notification);

        List<NotificationRecipient> recipients = admins.stream()
                .map(admin -> NotificationRecipient.builder()
                        .notification(savedNotification)
                        .recipient(admin)
                        .build())
                .toList();
        notificationRecipientRepository.saveAll(recipients);

        return savedNotification.getId();
    }

    private Jockey findJockeyById(Integer jockeyId) {
        User user = userRepository.findById(jockeyId)
                .orElseThrow(() -> new IllegalArgumentException("Jockey not found with id: " + jockeyId));
        Object unproxiedUser = org.hibernate.Hibernate.unproxy(user);

        if (!UserRole.JOCKEY.name().equals(user.getRole()) || !(unproxiedUser instanceof Jockey jockey)) {
            throw new IllegalArgumentException("User with id " + jockeyId + " is not a jockey");
        }
        return jockey;
    }
}
