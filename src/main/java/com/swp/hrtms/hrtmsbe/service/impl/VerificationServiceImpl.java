package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.JockeyCertCreateRequest;
import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.repository.*;
import com.swp.hrtms.hrtmsbe.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        public List<JockeyCert> getJockeyVerificationRequests(Integer recipientId) {
                return jockeyCertRepository.findAll();
        }

        @Override
        @Transactional(readOnly = true)
        public List<JockeyCert> getPendingCertificateImages(
                        Integer jockeyId) {
                return jockeyCertRepository.findPendingCertificatesByJockeyId(jockeyId);
        }

        @Override
        @Transactional
        // Khai: Store the frontend Base64 value directly as text.
        public JockeyCert createJockeyCertificate(JockeyCertCreateRequest request) {
                Jockey jockey = findJockeyById(request.getJockeyId());

                if (request == null || request.getCertName() == null || request.getCertName().isBlank()) {
                        throw new IllegalArgumentException("Certificate name cannot be empty");
                }
                if (request.getCertImageBase64() == null || request.getCertImageBase64().isBlank()) {
                        throw new IllegalArgumentException("Certificate image cannot be empty");
                }

                JockeyCert certificate = JockeyCert.builder()
                                .certName(request.getCertName().trim())
                                .certImageBase64(request.getCertImageBase64())
                                .issuedAt(request.getIssuedAt())
                                // khai
                                .status(request.getStatus() != null ? request.getStatus()
                                                : com.swp.hrtms.hrtmsbe.enums.CertificateStatus.PENDING)
                                .jockey(jockey)
                                .build();

                return jockeyCertRepository.save(certificate);
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

        @Override
        @Transactional
        public List<JockeyCert> acceptJockeyCertificates(Integer jockeyId, Integer adminId) {
                // 1. Fetch pending certificates and update status
                List<JockeyCert> certs = jockeyCertRepository
                                .findPendingCertificatesByJockeyId(jockeyId);
                if (certs.isEmpty()) {
                        throw new RuntimeException("No pending certificates found for the given jockey.");
                }
                for (JockeyCert cert : certs) {
                        // BR_new_FastTrack (Tiền đề): Cấp trạng thái 'VERIFIED' (đã kiểm duyệt chứng
                        // chỉ)
                        // để nài ngựa đủ điều kiện tham gia luồng "Cập nhật siêu tốc" sau này.
                        // BR_01 (Tiền đề): Xác nhận chứng chỉ hợp lệ để hệ thống đối chiếu "Khớp Loại
                        // ngựa" khi đăng ký.
                        // khai
                        cert.setStatus(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.VERIFIED);
                }
                certs = jockeyCertRepository.saveAll(certs);// luu duoc "Dong Y"

                // 2. Mark original notification as 'Accept'
                notificationRecipientRepository.markVerificationRequestAsAccepted(adminId, jockeyId);

                // 2.5 Mark other admins' notifications as 'DONE_VERIFY'
                notificationRecipientRepository.markAllOtherVerificationRequestsAsDoneVerify(jockeyId);

                // 2.6 Update the type of the original notification
                notificationRepository.updateTypeToDoneVerify(jockeyId);

                // 3. Send acceptance notification to the jockey
                User admin = userRepository.findById(adminId)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));
                User jockey = userRepository.findById(jockeyId)
                                .orElseThrow(() -> new RuntimeException("Jockey not found"));

                Notification notification = Notification
                                .builder()
                                .sender(admin)
                                .title("Certificate Verified")
                                .content("Your certificates have been verified successfully.")
                                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.ACCEPT_CERTIFICATE)
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                notification = notificationRepository.save(notification);

                NotificationRecipient recipient = NotificationRecipient
                                .builder()
                                .notification(notification)
                                .recipient(jockey)
                                // khai
                                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                                .build();
                notificationRecipientRepository.save(recipient);
                return certs;
        }

        @Override
        @Transactional
        public List<JockeyCert> rejectJockeyCertificates(Integer jockeyId, Integer adminId, String reason) {
                // 1. Fetch pending certificates and update status
                List<JockeyCert> certs = jockeyCertRepository
                                .findPendingCertificatesByJockeyId(jockeyId);
                if (certs.isEmpty()) {
                        throw new RuntimeException("No pending certificates found for the given jockey.");
                }
                for (JockeyCert cert : certs) {
                        // khai
                        cert.setStatus(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.REJECTED);
                }
                certs = jockeyCertRepository.saveAll(certs);//luu bi "Tu Choi"

                // 2. Mark original notification as 'Reject'
                notificationRecipientRepository.markVerificationRequestAsRejected(adminId, jockeyId);

                // 1.5 Mark other admins' notifications as 'DONE_VERIFY'
                notificationRecipientRepository.markAllOtherVerificationRequestsAsDoneVerify(jockeyId);

                // 1.6 Update the type of the original notification
                notificationRepository.updateTypeToDoneVerify(jockeyId);

                // 3. Send rejection notification to the jockey
                User admin = userRepository.findById(adminId)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));
                User jockey = userRepository.findById(jockeyId)
                                .orElseThrow(() -> new RuntimeException("Jockey not found"));

                Notification notification = Notification
                                .builder()
                                .sender(admin)
                                .title("Certificate Verification Rejected")
                                .content(reason)
                                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.REJECT_CERTIFICATE)
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                notification = notificationRepository.save(notification);

                NotificationRecipient recipient = NotificationRecipient
                                .builder()
                                .notification(notification)
                                .recipient(jockey)
                                // khai
                                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                                .build();
                notificationRecipientRepository.save(recipient);
                return certs;
        }
}
