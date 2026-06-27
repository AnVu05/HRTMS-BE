package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyCertificateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.JockeyVerificationRequestResponse;
import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.entity.JockeyCert;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.swp.hrtms.hrtmsbe.repository.JockeyRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

        private static final String NOTIFICATION_TITLE = "Certificate Verification Request";
        private static final String NOTIFICATION_CONTENT = "A jockey has requested verification for all pending certificates.";
        private final NotificationRecipientRepository notificationRecipientRepository;
        private final JockeyCertRepository jockeyCertRepository;
        private final NotificationRepository notificationRepository;
        private final UserRepository userRepository;
        private final AdminRepository adminRepository;
        private final JockeyRepository jockeyRepository;

        @Override
        @Transactional(readOnly = true)
        public List<JockeyVerificationRequestResponse> getJockeyVerificationRequests(Integer recipientId) {
                List<NotificationRecipient> recipients = notificationRecipientRepository
                                .findPendingVerificationRequests(recipientId);
                List<JockeyVerificationRequestResponse> responses = new ArrayList<>();

                for (NotificationRecipient nr : recipients) {
                        org.hibernate.Hibernate.initialize(nr.getNotification().getSender());
                        Object unproxiedSender = org.hibernate.Hibernate.unproxy(nr.getNotification().getSender());
                        JockeyCert certificate = nr.getNotification().getJockeyCert();
                        if (unproxiedSender instanceof Jockey && certificate != null) {
                                Jockey jockey = (Jockey) unproxiedSender;

                                JockeyVerificationRequestResponse response = JockeyVerificationRequestResponse
                                                .builder()
                                                .notificationId(nr.getNotification().getId())
                                                .jockeyId(jockey.getId())
                                                .jockeyName(jockey.getJockeyName())
                                                .pendingCertificates(List.of(toCertificateResponse(certificate)))
                                                .build();
                                responses.add(response);
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

                JockeyCert savedCertificate = jockeyCertRepository.save(certificate);
                createCertificateVerificationNotification(jockey, savedCertificate);

                return savedCertificate.getId();
        }

        @Override
        @Transactional
        // Khai: Create a notification from the jockey and one unread recipient row per
        // admin.
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
                return jockeyRepository.findById(jockeyId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Jockey not found with id: " + jockeyId));
        }

        // Khai: Accept only one selected pending certificate instead of every pending certificate.
        @Override
        @Transactional
        public void acceptJockeyCertificate(Integer jockeyId, Integer certId, Integer adminId) {
                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert = jockeyCertRepository
                                .findCertificateByIdAndJockeyId(certId, jockeyId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Certificate not found with id " + certId + " for jockey " + jockeyId));
                if (cert.getStatus() != null && !"PENDING".equals(cert.getStatus())) {
                        throw new RuntimeException("Certificate is not pending.");
                }

                cert.setStatus("VERIFIED");
                jockeyCertRepository.save(cert);

                // Khai: Close only this certificate verification notification.
                notificationRecipientRepository.markCertificateVerificationAsAccepted(adminId, certId);
                notificationRecipientRepository.markOtherCertificateVerificationRequestsAsDone(adminId, certId);
                notificationRepository.updateCertificateNotificationTypeToDoneVerify(certId);

                com.swp.hrtms.hrtmsbe.entity.User admin = userRepository.findById(adminId)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));
                com.swp.hrtms.hrtmsbe.entity.User jockey = userRepository.findById(jockeyId)
                                .orElseThrow(() -> new RuntimeException("Jockey not found"));

                com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                                .builder()
                                .sender(admin)
                                .title("Certificate Verified")
                                .content("Your certificate '" + cert.getCertName()
                                                + "' has been verified successfully.")
                                .type("ACCEPT_CERTIFICATE")
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                notification = notificationRepository.save(notification);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                                .builder()
                                .notification(notification)
                                .recipient(jockey)
                                .status("None")
                                .build();
                notificationRecipientRepository.save(recipient);
        }

        // Khai: Reject only one selected pending certificate instead of every pending certificate.
        @Override
        @Transactional
        public void rejectJockeyCertificate(Integer jockeyId, Integer certId, Integer adminId, String reason) {
                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert = jockeyCertRepository
                                .findCertificateByIdAndJockeyId(certId, jockeyId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Certificate not found with id " + certId + " for jockey " + jockeyId));
                if (cert.getStatus() != null && !"PENDING".equals(cert.getStatus())) {
                        throw new RuntimeException("Certificate is not pending.");
                }

                cert.setStatus("REJECTED");
                jockeyCertRepository.save(cert);

                // Khai: Close only this certificate verification notification.
                notificationRecipientRepository.markCertificateVerificationAsRejected(adminId, certId);
                notificationRecipientRepository.markOtherCertificateVerificationRequestsAsDone(adminId, certId);
                notificationRepository.updateCertificateNotificationTypeToDoneVerify(certId);

                com.swp.hrtms.hrtmsbe.entity.User admin = userRepository.findById(adminId)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));
                com.swp.hrtms.hrtmsbe.entity.User jockey = userRepository.findById(jockeyId)
                                .orElseThrow(() -> new RuntimeException("Jockey not found"));

                String rejectContent = "Your certificate '" + cert.getCertName() + "' has been rejected.";
                if (reason != null && !reason.isBlank()) {
                        rejectContent = rejectContent + " Reason: " + reason;
                }

                com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                                .builder()
                                .sender(admin)
                                .title("Certificate Verification Rejected")
                                .content(rejectContent)
                                .type("REJECT_CERTIFICATE")
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                notification = notificationRepository.save(notification);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                                .builder()
                                .notification(notification)
                                .recipient(jockey)
                                .status("None")
                                .build();
                notificationRecipientRepository.save(recipient);
        }

        // Khai: Create one verification notification for one newly submitted certificate.
        private void createCertificateVerificationNotification(Jockey jockey, JockeyCert certificate) {
                List<Admin> admins = adminRepository.findAll();
                if (admins.isEmpty()) {
                        throw new IllegalArgumentException("No admin account is available to receive the request");
                }

                Notification notification = Notification.builder()
                                .sender(jockey)
                                .title(NOTIFICATION_TITLE)
                                .content("Jockey has requested verification for certificate '"
                                                + certificate.getCertName() + "'.")
                                .type("VERIFY_CERTIFICATE")
                                .jockeyCert(certificate)
                                .build();
                Notification savedNotification = notificationRepository.save(notification);

                List<NotificationRecipient> recipients = admins.stream()
                                .map(admin -> NotificationRecipient.builder()
                                                .notification(savedNotification)
                                                .recipient(admin)
                                                .build())
                                .toList();
                notificationRecipientRepository.saveAll(recipients);
        }

        // Khai: Map the pending certificate so admin can pass cert_id to accept/reject APIs.
        private JockeyCertificateResponse toCertificateResponse(JockeyCert certificate) {
                return JockeyCertificateResponse.builder()
                                .certId(certificate.getId())
                                .certName(certificate.getCertName())
                                .certImageBase64(certificate.getCertImg())
                                .status(certificate.getStatus())
                                .build();
        }

        /*
        // Khai: Old function accepted every pending certificate of one jockey.
        @Override
        @Transactional
        public void acceptJockeyCertificates(Integer jockeyId, Integer adminId) {
                // 1. Fetch pending certificates and update status
                List<com.swp.hrtms.hrtmsbe.entity.JockeyCert> certs = jockeyCertRepository
                                .findPendingCertificatesByJockeyId(jockeyId);
                if (certs.isEmpty()) {
                        throw new RuntimeException("No pending certificates found for the given jockey.");
                }
                for (com.swp.hrtms.hrtmsbe.entity.JockeyCert cert : certs) {
                        // BR_new_FastTrack (Tiền đề): Cấp trạng thái 'VERIFIED' (đã kiểm duyệt chứng
                        // chỉ)
                        // để nài ngựa đủ điều kiện tham gia luồng "Cập nhật siêu tốc" sau này.
                        // BR_01 (Tiền đề): Xác nhận chứng chỉ hợp lệ để hệ thống đối chiếu "Khớp Loại
                        // ngựa" khi đăng ký.
                        cert.setStatus("VERIFIED");
                }
                jockeyCertRepository.saveAll(certs);

                // 2. Mark original notification as 'Accept'
                notificationRecipientRepository.markVerificationRequestAsAccepted(adminId, jockeyId);

                // 2.5 Mark other admins' notifications as 'DONE_VERIFY'
                notificationRecipientRepository.markAllOtherVerificationRequestsAsDoneVerify(jockeyId);

                // 2.6 Update the type of the original notification
                notificationRepository.updateTypeToDoneVerify(jockeyId);

                // 3. Send acceptance notification to the jockey
                com.swp.hrtms.hrtmsbe.entity.User admin = userRepository.findById(adminId)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));
                com.swp.hrtms.hrtmsbe.entity.User jockey = userRepository.findById(jockeyId)
                                .orElseThrow(() -> new RuntimeException("Jockey not found"));

                com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                                .builder()
                                .sender(admin)
                                .title("Certificate Verified")
                                .content("Your certificates have been verified successfully.")
                                .type("ACCEPT_CERTIFICATE")
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                notification = notificationRepository.save(notification);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                                .builder()
                                .notification(notification)
                                .recipient(jockey)
                                .status("None")
                                .build();
                notificationRecipientRepository.save(recipient);
        }

        // Khai: Old function rejected every pending certificate of one jockey.
        @Override
        @Transactional
        public void rejectJockeyCertificates(Integer jockeyId, Integer adminId, String reason) {
                // 1. Fetch pending certificates and update status
                List<com.swp.hrtms.hrtmsbe.entity.JockeyCert> certs = jockeyCertRepository
                                .findPendingCertificatesByJockeyId(jockeyId);
                if (certs.isEmpty()) {
                        throw new RuntimeException("No pending certificates found for the given jockey.");
                }
                for (com.swp.hrtms.hrtmsbe.entity.JockeyCert cert : certs) {
                        cert.setStatus("REJECTED");
                }
                jockeyCertRepository.saveAll(certs);

                // 2. Mark original notification as 'Reject'
                notificationRecipientRepository.markVerificationRequestAsRejected(adminId, jockeyId);

                // 1.5 Mark other admins' notifications as 'DONE_VERIFY'
                notificationRecipientRepository.markAllOtherVerificationRequestsAsDoneVerify(jockeyId);

                // 1.6 Update the type of the original notification
                notificationRepository.updateTypeToDoneVerify(jockeyId);

                // 3. Send rejection notification to the jockey
                com.swp.hrtms.hrtmsbe.entity.User admin = userRepository.findById(adminId)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));
                com.swp.hrtms.hrtmsbe.entity.User jockey = userRepository.findById(jockeyId)
                                .orElseThrow(() -> new RuntimeException("Jockey not found"));

                com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                                .builder()
                                .sender(admin)
                                .title("Certificate Verification Rejected")
                                .content(reason)
                                .type("REJECT_CERTIFICATE")
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                notification = notificationRepository.save(notification);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                                .builder()
                                .notification(notification)
                                .recipient(jockey)
                                .status("None")
                                .build();
                notificationRecipientRepository.save(recipient);
        }
        */
}
