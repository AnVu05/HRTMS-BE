package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.TournamentCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.TournamentCancelRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ActiveTournamentResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentDashboardResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentResponse;
import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.service.TournamentService;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;

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
        //Khai
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Tournament name is required");
        }

        // Find Admin
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with id: " + adminId));

        //Khai: Validate all required dates in the order used by the FE workflow.
        if (request.getAnnouncementDate() == null || request.getRegistrationOpenDate() == null ||
                request.getRegistrationCloseDate() == null || request.getStartDate() == null ||
                request.getEndDate() == null) {
            throw new IllegalArgumentException(
                    "Announcement date, registration open date, registration close date, start date, and end date are required");
        }

        if (request.getAnnouncementDate().isAfter(request.getRegistrationOpenDate())) {
            throw new IllegalArgumentException("Announcement date cannot be after registration open date");
        }
        if (request.getRegistrationOpenDate().isAfter(request.getRegistrationCloseDate())) {
            throw new IllegalArgumentException("Registration open date cannot be after registration close date");
        }
        if (!request.getRegistrationCloseDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Registration close date must be before tournament start date");
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Tournament start date cannot be after end date");
        }

        // Create Tournament entity
        Tournament tournament = Tournament.builder()
                .admin(admin)
                .name(request.getName().trim())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                //Khai
                .announcementDate(request.getAnnouncementDate())
                .registrationOpenDate(request.getRegistrationOpenDate())
                .registrationCloseDate(request.getRegistrationCloseDate())
                .description(request.getDescription())
                .status("UPCOMING")
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
        // Chỉ lấy các giải đấu đang ở trạng thái PUBLISHED theo yêu cầu
        List<Tournament> tournaments = tournamentRepository.findByStatus("PUBLISHED");

        boolean updated = false;
        for (Tournament tournament : tournaments) {
            if (tournament.getStartDate() == null || tournament.getEndDate() == null)
                continue;

            String currentStatus = tournament.getStatus();
            String newStatus = currentStatus;

            if (now.isBefore(tournament.getStartDate())) {
                newStatus = "UPCOMING";
            } else if (now.isAfter(tournament.getEndDate())) {
                newStatus = "FINISHED";
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
    public List<TournamentDashboardResponse> getTournamentsForDashboard() {
        return tournamentRepository.getTournamentsForDashboard();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActiveTournamentResponse> getActiveTournaments() {
        return tournamentRepository.findByStatus("PUBLISHED").stream()
                .map(t -> ActiveTournamentResponse.builder()
                        .id(t.getId())
                        .name(t.getName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TournamentResponse updateTournament(Integer id,
            com.swp.hrtms.hrtmsbe.dto.request.TournamentUpdateRequest request) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (request.getStartDate() != null) {
            tournament.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            tournament.setEndDate(request.getEndDate());
        }

        if (tournament.getStartDate() != null && tournament.getEndDate() != null
                && tournament.getStartDate().isAfter(tournament.getEndDate())) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        if (request.getName() != null) {
            tournament.setName(request.getName());
        }
        if (request.getAllowedBreed() != null) {
            tournament.setAllowedBreed(request.getAllowedBreed());
        }
        if (request.getAllowedHorseAge() != null) {
            tournament.setAllowedHorseAge(request.getAllowedHorseAge());
        }
        if (request.getDescription() != null) {
            tournament.setDescription(request.getDescription());
        }
        String oldStatus = tournament.getStatus();
        if (request.getStatus() != null) {
            tournament.setStatus(request.getStatus());
        }

        tournament = tournamentRepository.save(tournament);

        if (!"PUBLISHED".equals(oldStatus) && "PUBLISHED".equals(request.getStatus())) {
            List<User> targetUsers = userRepository.findByRoleIn(Arrays.asList("JOCKEY", "HORSE_OWNER", "SPECTATOR"));
            if (!targetUsers.isEmpty()) {
                Notification notification = Notification.builder()
                        .sender(null) // System notification
                        .title("New Tournament " + tournament.getName())
                        .content(tournament.getDescription())
                        .type("None")
                        .build();
                notification = notificationRepository.save(notification);

                List<NotificationRecipient> recipients = new ArrayList<>();
                for (User user : targetUsers) {
                    NotificationRecipient recipient = NotificationRecipient.builder()
                            .notification(notification)
                            .recipient(user)
                            .status("None")
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

        tournament.setStatus("CANCELLED");
        tournament.setCancelReason(request.getReason());
        tournamentRepository.save(tournament);

        List<Race> races = raceRepository.findByTournamentId(tournamentId);
        for (Race race : races) {
            race.setStatus("CANCELLED");
        }
        raceRepository.saveAll(races);

        return "Tournament and all related races have been successfully cancelled.";
    }

    private TournamentResponse mapToResponse(Tournament tournament) {
        return TournamentResponse.builder()
                .id(tournament.getId())
                .adminId(tournament.getAdmin().getId())
                .name(tournament.getName())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                //Khai
                .announcementDate(tournament.getAnnouncementDate())
                .registrationOpenDate(tournament.getRegistrationOpenDate())
                .registrationCloseDate(tournament.getRegistrationCloseDate())
                .allowedBreed(tournament.getAllowedBreed())
                .allowedHorseAge(tournament.getAllowedHorseAge())
                .description(tournament.getDescription())
                .status(tournament.getStatus())
                .cancelReason(tournament.getCancelReason())
                .build();
    }
}
