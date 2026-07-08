package com.swp.hrtms.hrtmsbe.service.impl;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.response.NotificationResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeInvitationResponse;
import com.swp.hrtms.hrtmsbe.dto.request.RespondInvitationRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HorseOwnerNotificationResponse;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.JockeyRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.DoctorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

        private static final List<com.swp.hrtms.hrtmsbe.enums.NotificationType> CERTIFICATE_NOTIFICATION_TYPES = List.of(
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.ACCEPT_CERTIFICATE,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.REJECT_CERTIFICATE);

        private static final List<com.swp.hrtms.hrtmsbe.enums.NotificationType> ADMIN_NOTIFICATION_TYPES = List.of(
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_ACCEPTED,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_REJECTED,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_VERIFY,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.VERIFI_CERTIFICATE,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_ACCEPTED,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_REJECTED);

        private final NotificationRecipientRepository notificationRecipientRepository;
        private final JockeyRepository jockeyRepository;
        private final AdminRepository adminRepository;
        private final DoctorRepository doctorRepository;
        private static final List<com.swp.hrtms.hrtmsbe.enums.NotificationType> HORSE_OWNER_ADMIN_NOTIFICATION_TYPES = List.of(
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.NEW_TOURNAMENT,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.TOURNAMENT_UPDATE,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.TOURNAMENT_CANCELLED,

                        com.swp.hrtms.hrtmsbe.enums.NotificationType.NEW_RACE,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.RACE_UPDATE,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.RACE_CANCELLED,

                        com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_APPROVED,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_REJECTED);

        private static final List<com.swp.hrtms.hrtmsbe.enums.NotificationType> HORSE_OWNER_JOCKEY_NOTIFICATION_TYPES = List.of(
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.INVITATION_ACCEPTED,
                        com.swp.hrtms.hrtmsbe.enums.NotificationType.INVITATION_REJECTED);
        private final HorseOwnerRepository horseOwnerRepository;
        private final RefereeRepository refereeRepository;
        private final RaceRepository raceRepository;
        private final NotificationRepository notificationRepository;

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
        @Transactional
        public List<NotificationResponse> getDoctorInvitations(Integer doctorId) {
                if (!doctorRepository.existsById(doctorId)) {
                        throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
                }

                List<NotificationRecipient> invitations = notificationRecipientRepository
                                .findByRecipient_IdAndNotification_TypeOrderByNotification_CreatedAtDesc(
                                                doctorId,
                                                com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_INVITATION);

                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                invitations.stream()
                                .filter(nr -> nr.getStatus() == com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                                .forEach(nr -> {
                                        nr.setStatus(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.READ);
                                        nr.setReadAt(now);
                                });
                notificationRecipientRepository.saveAll(invitations);

                return invitations.stream().map(this::toResponse).toList();
        }

        @Override
        @Transactional(readOnly = true)
        public Page<NotificationResponse> getAdminNotifications(Integer adminId, int page, int size,
                        Boolean unreadOnly) {
                if (!adminRepository.existsById(adminId)) {
                        throw new ResourceNotFoundException("Admin not found with id: " + adminId);
                }

                Pageable pageable = PageRequest.of(page, size);

                if (Boolean.TRUE.equals(unreadOnly)) {
                        return notificationRecipientRepository
                                        .findByRecipient_IdAndNotification_TypeInAndStatusAndReadAtIsNullOrderByNotification_CreatedAtDesc(
                                                        adminId,
                                                        ADMIN_NOTIFICATION_TYPES,
                                                        com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD,
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
        public List<NotificationResponse> markAllAdminNotificationsAsRead(Integer adminId) {
                if (!adminRepository.existsById(adminId)) {
                        throw new ResourceNotFoundException("Admin not found with id: " + adminId);
                }

                Page<NotificationRecipient> unreadPage = notificationRecipientRepository
                                .findByRecipient_IdAndNotification_TypeInAndStatusAndReadAtIsNullOrderByNotification_CreatedAtDesc(
                                                adminId,
                                                ADMIN_NOTIFICATION_TYPES,
                                                com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD,
                                                org.springframework.data.domain.Pageable.unpaged());
                List<NotificationRecipient> unread = unreadPage.getContent();

                notificationRecipientRepository.markAllAsReadByRecipientId(adminId);

                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                unread.forEach(nr -> {
                        nr.setStatus(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.READ);
                        nr.setReadAt(now);
                });

                return unread.stream().map(this::toResponse).toList();
        }

        @Override
        @Transactional
        public List<NotificationResponse> markNotificationsAsReadByRecipient(Integer recipientId) {
            // First fetch all unread notifications to return them in the response
            List<NotificationRecipient> unreadRecipients = notificationRecipientRepository
                    .findByRecipient_IdAndNotification_TypeInAndStatusAndReadAtIsNullOrderByNotification_CreatedAtDesc(
                            recipientId,
                            java.util.List.of(
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_INVITATION,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_ACCEPTED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_REJECTED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.NEW_TOURNAMENT,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.TOURNAMENT_UPDATE,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.TOURNAMENT_CANCELLED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.NEW_RACE,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.RACE_UPDATE,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.RACE_CANCELLED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_APPROVED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_REJECTED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_VERIFY,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.VERIFI_CERTIFICATE,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_ACCEPTED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_REJECTED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.INVITATION_ACCEPTED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.INVITATION_REJECTED,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.ACCEPT_CERTIFICATE,
                                com.swp.hrtms.hrtmsbe.enums.NotificationType.REJECT_CERTIFICATE
                            ),
                            com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD,
                            org.springframework.data.domain.Pageable.unpaged()
                    ).getContent();

            // Perform the bulk update
            notificationRecipientRepository.markAllAsReadByRecipientId(recipientId);

            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            return unreadRecipients.stream().map(nr -> {
                nr.setStatus(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.READ);
                nr.setReadAt(now);
                return toResponse(nr);
            }).toList();
        }

        private NotificationResponse toResponse(com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient) {
                Notification notification = recipient.getNotification();
                return toResponse(notification, recipient);
        }

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

        private NotificationResponse toResponse(Notification notification, NotificationRecipient recipient) {
                return NotificationResponse.builder()
                                .id(notification.getId())
                                .senderId(notification.getSender() != null ? notification.getSender().getId() : null)
                                .title(notification.getTitle())
                                .content(notification.getContent())
                                .createdAt(notification.getCreatedAt())
                                .type(notification.getType())
                                .raceId(notification.getRace() != null ? notification.getRace().getId() : null)
                                .recipientRecordId(recipient.getId())
                                .recipientId(recipient.getRecipient() != null ? recipient.getRecipient().getId() : null)
                                .status(recipient.getStatus())
                                .readAt(recipient.getReadAt())
                                .build();
        }

        private HorseOwnerNotificationResponse toHorseOwnerResponse(NotificationRecipient recipient) {
                Notification notification = recipient.getNotification();

                return HorseOwnerNotificationResponse.builder()
                                .notificationId(notification.getId())
                                .senderId(notification.getSender() != null ? notification.getSender().getId() : null)
                                .title(notification.getTitle())
                                .content(notification.getContent())
                                .type(notification.getType())
                                .raceId(notification.getRace() != null ? notification.getRace().getId() : null)
                                .createdAt(notification.getCreatedAt())
                                .recipientRecordId(recipient.getId())
                                .recipientId(recipient.getRecipient() != null ? recipient.getRecipient().getId() : null)
                                .status(recipient.getStatus())
                                .readAt(recipient.getReadAt())
                                .build();
        }

        /**
         * Lấy danh sách lời mời đang chờ (Pending Invitations) của Trọng tài.
         * Chỉ lấy các lời mời có trạng thái "None" và loại thông báo
         * "REFEREE_INVITATION".
         *
         * @param refereeId ID của Trọng tài
         * @return Danh sách DTO chứa thông tin lời mời
         */
        @Override
        @Transactional(readOnly = true)
        public List<RefereeInvitationResponse> getPendingRefereeInvitations(Integer refereeId) {
                // Kiểm tra sự tồn tại của Trọng tài trong cơ sở dữ liệu
                if (!refereeRepository.existsById(refereeId)) {
                        throw new ResourceNotFoundException("Referee not found with id: " + refereeId);
                }

                // Lấy tất cả các lời mời đang chờ (trạng thái None) của Trọng tài này
                List<NotificationRecipient> recipients = notificationRecipientRepository
                                .findPendingRefereeInvitations(refereeId);

                // Ánh xạ danh sách các lời mời sang DTO để trả về cho Client
                return recipients.stream().map(recipient -> {
                        Notification notification = recipient.getNotification();
                        Race race = notification.getRace();
                        String tournamentName = (race != null && race.getTournament() != null)
                                        ? race.getTournament().getName()
                                        : null;

                        return RefereeInvitationResponse.builder()
                                        .notificationId(notification.getId())
                                        .senderId(notification.getSender() != null ? notification.getSender().getId()
                                                        : null)
                                        .title(notification.getTitle())
                                        .content(notification.getContent())
                                        .type(notification.getType())
                                        .createdAt(notification.getCreatedAt())
                                        .recipientRecordId(recipient.getId())
                                        .recipientId(recipient.getRecipient() != null ? recipient.getRecipient().getId()
                                                        : null)
                                        .status(recipient.getStatus())
                                        .readAt(recipient.getReadAt())
                                        .raceId(race != null ? race.getId() : null)
                                        .raceName(race != null ? race.getName() : null)
                                        .tournamentName(tournamentName)
                                        .date(race != null ? race.getDate() : null)
                                        .startTime(race != null ? race.getStartTime() : null)
                                        .endTime(race != null ? race.getEndTime() : null)
                                        .distanceM(race != null ? race.getDistanceM() : null)
                                        .build();
                }).toList();
        }

        /**
         * Trọng tài phản hồi lời mời tham gia điều hành cuộc đua (Accept hoặc Reject).
         * Khi Accept, hệ thống bắt buộc kiểm tra xem trọng tài đã có lịch thi đấu nào
         * khác trùng thời gian hay chưa.
         * Nếu trùng lịch, ném lỗi và từ chối xử lý.
         * Nếu không trùng:
         * - Trạng thái của NotificationRecipient được đổi thành "Accept", trạng thái
         * Cuộc đua thành "PUBLISHED".
         * - Khi Reject, trạng thái đổi thành "Reject", trạng thái Cuộc đua thành
         * "REJECTED".
         *
         * @param refereeId      ID của Trọng tài
         * @param notificationId ID của NotificationRecipient nhận lời mời
         * @param request        DTO phản hồi chứa trạng thái mong muốn ("Accept" hoặc
         *                       "Reject")
         */
        @Override
        @Transactional
        public RefereeInvitationResponse respondToRefereeInvitation(Integer refereeId, Integer notificationId,
                        RespondInvitationRequest request) {
                // 1. Tìm Trọng tài trong hệ thống
                Referee referee = refereeRepository.findById(refereeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Referee not found with id: " + refereeId));

                // 2. Tìm thông báo nhận tương ứng của Trọng tài
                NotificationRecipient recipient = notificationRecipientRepository
                                .findByIdAndRecipient_Id(notificationId, refereeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Invitation not found for referee with id: " + notificationId));

                Notification notification = recipient.getNotification();
                Race race = notification.getRace();
                if (race == null) {
                        throw new ResourceNotFoundException("No race associated with this invitation.");
                }

                // Kiểm tra xem lời mời đã được xử lý hay chưa để tránh cập nhật lặp
                // Lời mời chỉ hợp lệ khi cuộc đua vẫn đang chờ trọng tài (PENDING_REFEREE) và
                // trọng tài được gán trùng khớp với refereeId
                if (!"PENDING_REFEREE".equals(race.getStatus() == null ? "" : race.getStatus().name()) || race.getReferee() == null
                                || !race.getReferee().getId().equals(refereeId)) {
                        throw new IllegalArgumentException(
                                        "This invitation is no longer valid or has already been responded to.");
                }

                String responseStatus = request.getStatus();
                if ("Accept".equalsIgnoreCase(responseStatus)) {
                        // --- VALIDATE TRÙNG LỊCH (BR_05: No scheduling conflicts) ---
                        // Kiểm tra xem trọng tài đã có lịch ở cuộc đua nào khác đang active (khác
                        // CANCELLED) trùng ngày và khoảng thời gian hay chưa
                        boolean hasOverlap = raceRepository.existsOverlappingForRefereeExcludingRace(
                                        refereeId,
                                        race.getId(),
                                        race.getDate(),
                                        race.getStartTime(),
                                        race.getEndTime());
                        if (hasOverlap) {
                                // Ném ngoại lệ thông báo lỗi trùng lịch biểu của Trọng tài như yêu cầu
                                throw new IllegalArgumentException(
                                                "Referee is already scheduled for another active/published race at this overlapping time.");
                        }

                        // Cập nhật trạng thái người nhận thành "None" (theo yêu cầu của hệ thống để hỗ
                        // trợ lọc thông báo chưa đọc sau này)
                        // Cập nhật trạng thái cuộc đua thành PUBLISHED (đã xuất bản)
                        recipient.setStatus(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.READ);
                        //khai
                        race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PREPARE);


                        // Tạo thông báo phản hồi (Đồng ý) gửi ngược về lại cho Admin
                        createResponseNotification(referee, race, true);
                } else if ("Reject".equalsIgnoreCase(responseStatus)) {
                        // Cập nhật trạng thái người nhận thành "None" theo yêu cầu
                        recipient.setStatus(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.READ);

                        // Tạo thông báo phản hồi (Từ chối) gửi ngược về lại cho Admin trước khi gán
                        // referee thành null
                        createResponseNotification(referee, race, false);

                        // Cập nhật trạng thái cuộc đua thành PENDING_REFEREE và gỡ bỏ referee_id (set
                        // null)
                        race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE);
                        race.setReferee(null);
                } else {
                        throw new IllegalArgumentException(
                                        "Invalid status. Supported statuses are 'Accept' or 'Reject'.");
                }

                // Lưu tất cả các thay đổi trạng thái vào cơ sở dữ liệu
                notification.setType(com.swp.hrtms.hrtmsbe.enums.NotificationType.DONE);
                notificationRepository.save(notification);

                recipient = notificationRecipientRepository.save(recipient);
                raceRepository.save(race);

                String tournamentName = (race.getTournament() != null) ? race.getTournament().getName() : null;
                return RefereeInvitationResponse.builder()
                                .notificationId(notification.getId())
                                .senderId(notification.getSender() != null ? notification.getSender().getId() : null)
                                .title(notification.getTitle())
                                .content(notification.getContent())
                                .type(notification.getType())
                                .createdAt(notification.getCreatedAt())
                                .recipientRecordId(recipient.getId())
                                .recipientId(recipient.getRecipient() != null ? recipient.getRecipient().getId() : null)
                                .status(recipient.getStatus())
                                .readAt(recipient.getReadAt())
                                .raceId(race.getId())
                                .raceName(race.getName())
                                .tournamentName(tournamentName)
                                .date(race.getDate())
                                .startTime(race.getStartTime())
                                .endTime(race.getEndTime())
                                .distanceM(race.getDistanceM())
                                .build();
        }

        // Hàm hỗ trợ tạo thông báo phản hồi gửi ngược lại cho Admin của cuộc đua
        private void createResponseNotification(Referee referee, Race race, boolean isAccepted) {
                User admin = race.getTournament().getAdmin();
                if (admin == null) {
                        return;
                }

                String title = isAccepted ? "Referee Invitation Accepted" : "Referee Invitation Rejected";
                String content = isAccepted
                                ? String.format("%s has accepted the invitation to referee the race '%s' in tournament '%s'.",
                                                referee.getName(), race.getName(), race.getTournament().getName())
                                : String.format("%s has rejected the invitation to referee the race '%s' in tournament '%s'.",
                                                referee.getName(), race.getName(), race.getTournament().getName());

                // Lưu thông báo mới (referee là người gửi)
                Notification notification = Notification.builder()
                                .sender(referee)
                                .title(title)
                                .content(content)
                                //khai
                                .type(isAccepted ? com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_ACCEPTED : com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_REJECTED)
                                .race(race)
                                .createdAt(java.time.LocalDateTime.now())
                                .build();
                notificationRepository.save(notification);

                // Liên kết thông báo phản hồi với Admin nhận (status mặc định là None)
                NotificationRecipient responseRecipient = NotificationRecipient.builder()
                                .notification(notification)
                                .recipient(admin)
                                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                                .build();
                notificationRecipientRepository.save(responseRecipient);
        }

    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 3600000) // run every hour
    @Transactional
    public void rejectExpiredRefereeInvitations() {
        java.time.LocalDateTime cutoff = java.time.LocalDateTime.now().minusHours(24);
        List<NotificationRecipient> expired = notificationRecipientRepository.findExpiredRefereeInvitations(cutoff);
        
        for (NotificationRecipient nr : expired) {
            Notification n = nr.getNotification();
            Race race = n.getRace();
            Referee referee = race.getReferee();
            if (referee == null) continue;

            // Mark invitation as READ/DONE
            nr.setStatus(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.READ);
            n.setType(com.swp.hrtms.hrtmsbe.enums.NotificationType.DONE);
            
            // Create response notification to Admin (type: REFEREE_REJECTED)
            createResponseNotification(referee, race, false);
            
            // Clear referee and keep race status PENDING_REFEREE
            race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE);
            race.setReferee(null);
            
            notificationRepository.save(n);
            notificationRecipientRepository.save(nr);
            raceRepository.save(race);
        }
    }
}
