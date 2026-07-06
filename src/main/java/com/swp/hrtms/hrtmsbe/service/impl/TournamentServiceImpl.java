package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCancelRequest;
import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ActiveTournamentResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;
import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.repository.*;
import com.swp.hrtms.hrtmsbe.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final AdminRepository adminRepository;
    private final RaceRepository raceRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @Override
    @Transactional
    public TournamentResponse createTournament(Integer adminId, TournamentCreateRequest request) {
        // Khai
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Tournament name is required");
        }

        // Find Admin
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with id: " + adminId));

        // Khai: Validate all required dates in the order used by the FE workflow.
        if (request.getPublishedDate() == null || request.getOpenPredictionDate() == null ||
                request.getClosePredictionDate() == null || request.getStartDate() == null ||
                request.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "Published date, open prediction date, close prediction date, start date, and end date are required");
        }

        if (request.getPublishedDate().isAfter(request.getOpenPredictionDate())) {
            throw new IllegalArgumentException("Published date cannot be after open prediction date");
        }
        if (request.getOpenPredictionDate().isAfter(request.getClosePredictionDate())) {
            throw new IllegalArgumentException("Open prediction date cannot be after close prediction date");
        }
        if (!request.getClosePredictionDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Close prediction date must be before tournament start date");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Tournament start date cannot be after end date");
        }

        // Create Tournament entity
        Tournament tournament = Tournament.builder()
                .admin(admin)
                .name(request.getName().trim())
                .createdAt(java.time.LocalDateTime.now())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                // Khai
                .publishedDate(request.getPublishedDate())
                .openPredictionDate(request.getOpenPredictionDate())
                .closePredictionDate(request.getClosePredictionDate())
                .status(request.getStatus() != null ? request.getStatus()
                        : com.swp.hrtms.hrtmsbe.enums.TournamentStatus.DRAFT)
                .build();

        // Save to DB
        tournament = tournamentRepository.save(tournament);

        // Map to Response DTO
        return mapToResponse(tournament);
    }

    @Override
    @Transactional
    // Chạy mỗi 5 giây để test: "*/5 * * * * ?"
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 0 * * ?")
    public void updateTournamentStatuses() {
        java.time.LocalDate now = java.time.LocalDate.now();
        // khai
        // Chỉ lấy các giải đấu đang ở trạng thái PUBLIC theo yêu cầu
        List<Tournament> tournaments = tournamentRepository
                .findByStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED);

        boolean updated = false;
        for (Tournament tournament : tournaments) {
            if (tournament.getStartDate() == null || tournament.getEndDate() == null)
                continue;

            com.swp.hrtms.hrtmsbe.enums.TournamentStatus currentStatus = tournament.getStatus();
            com.swp.hrtms.hrtmsbe.enums.TournamentStatus newStatus = currentStatus;

            if (now.isBefore(tournament.getStartDate())) {
                newStatus = com.swp.hrtms.hrtmsbe.enums.TournamentStatus.DRAFT;
            } else if (now.isAfter(tournament.getEndDate())) {
                newStatus = com.swp.hrtms.hrtmsbe.enums.TournamentStatus.COMPLETE;
            }
            // Nếu nằm trong khoảng đang diễn ra thì giữ nguyên (không đổi sang ONGOING)

            if (currentStatus == null || !currentStatus.equals(newStatus)) {
                tournament.setStatus(newStatus);
                updated = true;
            }
        }

        if (updated) {
            tournamentRepository.saveAll(tournaments);
        }
        System.out.println("Done update status");
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> getTournamentsForDashboard() {
        // khai
        return tournamentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiveTournamentResponse> getActiveTournaments() {
        // khai
        return tournamentRepository.findByStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED).stream()
                .map(t -> ActiveTournamentResponse.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getTournamentById(Integer id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));
        return mapToResponse(tournament);
    }

    @Override
    @Transactional
    public TournamentResponse updateTournament(Integer id,
            com.swp.hrtms.hrtmsbe.dto.request.TournamentUpdateRequest request) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        boolean isScheduleUpdated = false;

        if (request.getStartDate() != null && !request.getStartDate().equals(tournament.getStartDate())) {
            tournament.setStartDate(request.getStartDate());
            isScheduleUpdated = true;
        }
        if (request.getEndDate() != null && !request.getEndDate().equals(tournament.getEndDate())) {
            tournament.setEndDate(request.getEndDate());
            isScheduleUpdated = true;
        }
        // khai
        if (request.getPublishedDate() != null && !request.getPublishedDate().equals(tournament.getPublishedDate())) {
            tournament.setPublishedDate(request.getPublishedDate());
            isScheduleUpdated = true;
        }
        if (request.getOpenPredictionDate() != null
                && !request.getOpenPredictionDate().equals(tournament.getOpenPredictionDate())) {
            tournament.setOpenPredictionDate(request.getOpenPredictionDate());
            isScheduleUpdated = true;
        }
        if (request.getClosePredictionDate() != null
                && !request.getClosePredictionDate().equals(tournament.getClosePredictionDate())) {
            tournament.setClosePredictionDate(request.getClosePredictionDate());
            isScheduleUpdated = true;
        }

        if (tournament.getStartDate() != null && tournament.getEndDate() != null
                && tournament.getStartDate().isAfter(tournament.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (request.getName() != null) {
            tournament.setName(request.getName());
        }
        com.swp.hrtms.hrtmsbe.enums.TournamentStatus oldStatus = tournament.getStatus();
        if (request.getStatus() != null) {
            tournament.setStatus(request.getStatus());
        }

        tournament = tournamentRepository.save(tournament);

        // khai
        // khai
        if (oldStatus != com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED
                && tournament.getStatus() == com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED) {
            List<User> targetUsers = userRepository.findByRoleIn(Arrays.asList("JOCKEY", "HORSE_OWNER", "SPECTATOR"));
            if (!targetUsers.isEmpty()) {
                Notification notification = Notification.builder()
                        .sender(null) // System notification
                        .title("New Tournament " + tournament.getName())
                        .content("Tournament " + tournament.getName() + " is now published!")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.NEW_TOURNAMENT)
                        .build();
                notification = notificationRepository.save(notification);

                // code moi (06/07)
                // bo sung them gui thong bao
                List<NotificationRecipient> recipients = new ArrayList<>();
                for (User user : targetUsers) {
                    NotificationRecipient recipient = NotificationRecipient.builder()
                            .notification(notification)
                            .recipient(user)
                            .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                            .build();
                    recipients.add(recipient);
                }
                notificationRecipientRepository.saveAll(recipients);
            }
        } else if (isScheduleUpdated
                && tournament.getStatus() == com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED) {
            List<User> targetUsers = userRepository.findByRoleIn(Arrays.asList("JOCKEY", "HORSE_OWNER", "SPECTATOR"));
            if (!targetUsers.isEmpty()) {
                Notification notification = Notification.builder()
                        .sender(null) // System notification
                        .title("Tournament Schedule Updated: " + tournament.getName())
                        .content("The schedule for tournament '" + tournament.getName()
                                + "' has been updated. Please check the new timeline.")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.TOURNAMENT_UPDATE)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
                notification = notificationRepository.save(notification);

                List<NotificationRecipient> recipients = new ArrayList<>();
                for (User user : targetUsers) {
                    NotificationRecipient recipient = NotificationRecipient.builder()
                            .notification(notification)
                            .recipient(user)
                            .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                            .build();
                    recipients.add(recipient);
                }
                notificationRecipientRepository.saveAll(recipients);
            }
        }

        return mapToResponse(tournament);
    }

    @Override
    @Transactional
    public String cancelTournament(Integer tournamentId, TournamentCancelRequest request) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // khai
        // BR_17 (Tiền đề): Cập nhật trạng thái CANCELLED, chờ logic hoàn tiền (refund)
        // 100% Points/Vouchers.
        tournament.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.CANCELLED);
        tournament.setCancelReason(request.getReason());
        tournamentRepository.save(tournament);

        List<Race> races = raceRepository.findByTournamentId(tournamentId);
        for (Race race : races) {
            // BR_06: The cancelled race will be converted into a rest period.
            race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED);
        }
        raceRepository.saveAll(races);

        // code moi (06/07)
        // Gửi thông báo TOURNAMENT_CANCELLED cho JOCKEY, HORSE_OWNER, SPECTATOR
        List<User> targetUsers = userRepository.findByRoleIn(Arrays.asList("JOCKEY", "HORSE_OWNER", "SPECTATOR"));
        if (!targetUsers.isEmpty()) {
            Notification notification = Notification.builder()
                    .sender(null) // System notification
                    .title("Tournament Cancelled: " + tournament.getName())
                    .content("Tournament " + tournament.getName() + " has been cancelled. Reason: "
                            + request.getReason())
                    .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.TOURNAMENT_CANCELLED)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
            notification = notificationRepository.save(notification);

            List<NotificationRecipient> recipients = new ArrayList<>();
            for (User user : targetUsers) {
                NotificationRecipient recipient = NotificationRecipient.builder()
                        .notification(notification)
                        .recipient(user)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                recipients.add(recipient);
            }
            notificationRecipientRepository.saveAll(recipients);
        }

        return "Tournament and all related races have been successfully cancelled.";
    }

    private TournamentResponse mapToResponse(Tournament tournament) {
        return TournamentResponse.builder()
                .id(tournament.getId())
                .adminId(tournament.getAdmin().getId())
                .name(tournament.getName())
                .createdAt(tournament.getCreatedAt())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                // Khai
                .publishedDate(tournament.getPublishedDate())
                .openPredictionDate(tournament.getOpenPredictionDate())
                .closePredictionDate(tournament.getClosePredictionDate())
                .status(tournament.getStatus())
                .canceledAt(tournament.getCanceledAt())
                .reason(tournament.getCancelReason())
                .build();
    }
}
