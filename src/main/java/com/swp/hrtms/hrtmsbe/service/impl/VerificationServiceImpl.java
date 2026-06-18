package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final NotificationRecipientRepository notificationRecipientRepository;
    private final JockeyCertRepository jockeyCertRepository;
    private final com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository;

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
                base64Image = java.util.Base64.getEncoder().encodeToString(cert.getCertImg());
            }

            responses.add(com.swp.hrtms.hrtmsbe.dto.response.JockeyCertImageResponse.builder()
                    .certImageBase64(base64Image)
                    .build());
        }

        return responses;
    }

    @Override
    @Transactional
    public void acceptJockeyCertificates(Integer jockeyId, Integer adminId) {
        // 1. Fetch pending certificates and update status
        List<com.swp.hrtms.hrtmsbe.entity.JockeyCert> certs = jockeyCertRepository.findPendingCertificatesByJockeyId(jockeyId);
        if (certs.isEmpty()) {
            throw new RuntimeException("No pending certificates found for the given jockey.");
        }
        for (com.swp.hrtms.hrtmsbe.entity.JockeyCert cert : certs) {
            cert.setStatus("VERIFIED");
        }
        jockeyCertRepository.saveAll(certs);

        // 2. Mark original notification as 'Accept'
        notificationRecipientRepository.markVerificationRequestAsAccepted(adminId, jockeyId);

        // 3. Send acceptance notification to the jockey
        com.swp.hrtms.hrtmsbe.entity.User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        com.swp.hrtms.hrtmsbe.entity.User jockey = userRepository.findById(jockeyId)
                .orElseThrow(() -> new RuntimeException("Jockey not found"));

        com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                .sender(admin)
                .title("Certificate Verified")
                .content("Your certificates have been verified successfully.")
                .type("ACCEPT_CERTIFICATE")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        notification = notificationRepository.save(notification);

        com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient.builder()
                .notification(notification)
                .recipient(jockey)
                .status("None")
                .build();
        notificationRecipientRepository.save(recipient);
    }

    @Override
    @Transactional
    public void rejectJockeyCertificates(Integer jockeyId, Integer adminId, String reason) {
        // 1. Mark original notification as 'Reject'
        notificationRecipientRepository.markVerificationRequestAsRejected(adminId, jockeyId);

        // 2. Send rejection notification to the jockey
        com.swp.hrtms.hrtmsbe.entity.User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        com.swp.hrtms.hrtmsbe.entity.User jockey = userRepository.findById(jockeyId)
                .orElseThrow(() -> new RuntimeException("Jockey not found"));

        com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                .sender(admin)
                .title("Certificate Verification Rejected")
                .content(reason)
                .type("REJECT_CERTIFICATE")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        notification = notificationRepository.save(notification);

        com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient.builder()
                .notification(notification)
                .recipient(jockey)
                .status("None")
                .build();
        notificationRecipientRepository.save(recipient);
    }
}
