package com.swp.hrtms.hrtmsbe.service.impl;

// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest;
//Khai
import com.swp.hrtms.hrtmsbe.dto.response.RaceDashboardItem;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;
//Khai
import com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.enums.RaceStatus;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.service.RaceService;
import com.swp.hrtms.hrtmsbe.entity.Prediction;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.PredictionRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import com.swp.hrtms.hrtmsbe.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;
    private final TournamentRepository tournamentRepository;
    private final PredictionRepository predictionRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final RefereeRepository refereeRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final com.swp.hrtms.hrtmsbe.repository.RaceFormatRepository raceFormatRepository;
    private final com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository;
    private final com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository registrationFormRepository;
    private final com.swp.hrtms.hrtmsbe.repository.RaceResultRepository raceResultRepository;
    private final com.swp.hrtms.hrtmsbe.repository.RacePlacementRepository racePlacementRepository;

    @Override
    @Transactional
    public List<RaceResponse> createRacesBatch(RaceBatchCreateRequest request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tournament not found with id: " + request.getTournamentId()));

        List<Race> racesToSave = new ArrayList<>();

        for (int i = 0; i < request.getRaces().size(); i++) {
            RaceCreateRequest raceReq = request.getRaces().get(i);

            // Validation: Null checks and StartTime <= EndTime
            if (raceReq.getDate() == null || raceReq.getStartTime() == null || raceReq.getEndTime() == null) {
                throw new IllegalArgumentException("Race date, start time, and end time are required.");
            }
            if (raceReq.getStartTime().isAfter(raceReq.getEndTime())) {
                throw new IllegalArgumentException(
                        "Start time cannot be after end time for race '" + raceReq.getName() + "'.");
            }

            // 1. Validation: Overlap in DB (Tournament) (BR_05: No scheduling conflicts)
            if (raceRepository.existsOverlappingInTournament(tournament.getId(), raceReq.getDate(),
                    raceReq.getStartTime(), raceReq.getEndTime())) {
                throw new IllegalArgumentException(
                        "Race '" + raceReq.getName() + "' overlaps with an existing race in the tournament.");
            }

            // 2. Validation: Overlap within the incoming list (Tournament)
            for (int j = 0; j < i; j++) {
                RaceCreateRequest previousReq = request.getRaces().get(j);
                if (raceReq.getDate().equals(previousReq.getDate()) &&
                        raceReq.getStartTime().isBefore(previousReq.getEndTime()) &&
                        raceReq.getEndTime().isAfter(previousReq.getStartTime())) {
                    throw new IllegalArgumentException(
                            "Race '" + raceReq.getName() + "' overlaps with another race in the same request payload.");
                }
            }

            Referee referee = null;
            if (raceReq.getRefereeId() != null) {
                referee = refereeRepository.findById(raceReq.getRefereeId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Referee not found with id: " + raceReq.getRefereeId()));

                // 3. Validation: Overlap in DB (Referee) (BR_05: No scheduling conflicts)
                if (raceRepository.existsOverlappingForReferee(referee.getId(), raceReq.getDate(),
                        raceReq.getStartTime(), raceReq.getEndTime())) {
                    throw new IllegalArgumentException("Referee is already assigned to another overlapping race.");
                }

                // 4. Validation: Overlap within the incoming list (Referee)
                for (int j = 0; j < i; j++) {
                    RaceCreateRequest previousReq = request.getRaces().get(j);
                    if (raceReq.getRefereeId().equals(previousReq.getRefereeId()) &&
                            raceReq.getDate().equals(previousReq.getDate()) &&
                            raceReq.getStartTime().isBefore(previousReq.getEndTime()) &&
                            raceReq.getEndTime().isAfter(previousReq.getStartTime())) {
                        throw new IllegalArgumentException(
                                "Referee is assigned to overlapping races in the same request payload.");
                    }
                }
            }

            com.swp.hrtms.hrtmsbe.entity.RaceFormat raceRules = null;
            if (raceReq.getRaceRulesId() != null) {
                raceRules = raceFormatRepository.findById(raceReq.getRaceRulesId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Race rules not found with id: " + raceReq.getRaceRulesId()));
            }

            Race race = Race.builder()
                    .tournament(tournament)
                    .name(raceReq.getName())
                    .date(raceReq.getDate())
                    .startTime(raceReq.getStartTime())
                    .endTime(raceReq.getEndTime())
                    .distanceM(raceReq.getDistanceM())
                    .numHorse(raceReq.getNumHorse())
                    .referee(referee)
                    .status(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE)
                    .raceRules(raceRules)
                    .expectedDurationMinutes(raceReq.getExpectedDurationMinutes())
                    .breakTimeMinutes(raceReq.getBreakTimeMinutes())
                    .build();

            racesToSave.add(race);
        }

        racesToSave = raceRepository.saveAll(racesToSave);

        List<RaceResponse> responses = new ArrayList<>();
        for (Race r : racesToSave) {
            sendRefereeInvitation(r);

            responses.add(mapToRaceResponse(r));
        }

        return responses;
    }

    @Override
    @Transactional
    public RaceResponse createSingleRace(
            com.swp.hrtms.hrtmsbe.dto.request.RaceRequest request) {
        if (request.getTournamentId() == null || request.getDate() == null ||
                request.getStartTime() == null || request.getEndTime() == null ||
                request.getName() == null || request.getName().isBlank() ||
                request.getDistanceM() == null) {
            throw new IllegalArgumentException(
                    "Tournament ID, race name, date, distance, start time, and end time are required.");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException(
                    "Start time must be before end time for race '" + request.getName() + "'.");
        }

        if (request.getDistanceM() <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0 meters.");
        }

        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tournament not found with id: " + request.getTournamentId()));

        if (raceRepository.existsOverlappingInTournament(tournament.getId(), request.getDate(),
                request.getStartTime(), request.getEndTime())) {
            throw new IllegalArgumentException(
                    "Race '" + request.getName() + "' overlaps with an existing race in the tournament.");
        }

        Referee referee = null;
        if (request.getRefereeId() != null) {
            referee = refereeRepository.findById(request.getRefereeId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Referee not found with id: " + request.getRefereeId()));

            if (raceRepository.existsOverlappingForReferee(referee.getId(), request.getDate(),
                    request.getStartTime(), request.getEndTime())) {
                throw new IllegalArgumentException("Referee is already assigned to another overlapping race.");
            }
        }

        com.swp.hrtms.hrtmsbe.entity.RaceFormat raceRules = null;
        if (request.getRaceRulesId() != null) {
            raceRules = raceFormatRepository.findById(request.getRaceRulesId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Race rules not found with id: " + request.getRaceRulesId()));
        }

        Race race = Race.builder()
                .tournament(tournament)
                .name(request.getName())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .distanceM(request.getDistanceM())
                .numHorse(request.getNumHorse())
                .referee(referee)
                .status(request.getStatus() != null ? request.getStatus()
                        : com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE)
                .reason(request.getReason())
                .raceRules(raceRules)
                .expectedDurationMinutes(request.getExpectedDurationMinutes())
                .breakTimeMinutes(request.getBreakTimeMinutes())
                .canceledAt(request.getCanceledAt())
                .build();

        race = raceRepository.save(race);

        sendRefereeInvitation(race);

        return mapToRaceResponse(race);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RaceResponse> getAllRaces() {
        return raceRepository.findAll().stream().map(this::mapToRaceResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RaceResponse getRaceById(Integer id) {
        Race race = raceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + id));
        return mapToRaceResponse(race);
    }

    private RaceResponse mapToRaceResponse(Race race) {
        return RaceResponse.builder()
                .id(race.getId())
                .tournamentId(race.getTournament() != null ? race.getTournament().getId() : null)
                .name(race.getName())
                .date(race.getDate())
                .startTime(race.getStartTime())
                .endTime(race.getEndTime())
                .distanceM(race.getDistanceM())
                .numHorse(race.getNumHorse())
                .refereeId(race.getReferee() != null ? race.getReferee().getId() : null)
                .status(race.getStatus())
                .reason(race.getReason())
                .raceRulesId(race.getRaceRules() != null ? race.getRaceRules().getId() : null)
                .expectedDurationMinutes(race.getExpectedDurationMinutes())
                .breakTimeMinutes(race.getBreakTimeMinutes())
                .canceledAt(race.getCanceledAt())
                .build();
    }

    @Override
    @Transactional
    public RaceResponse updateRace(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceRequest request) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        com.swp.hrtms.hrtmsbe.enums.RaceStatus oldStatus = race.getStatus();
        boolean refereeChanged = false;

        if (request.getRefereeId() != null) {
            if (race.getReferee() == null || !race.getReferee().getId().equals(request.getRefereeId())) {
                refereeChanged = true;
            }

            Referee referee = refereeRepository.findById(request.getRefereeId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Referee not found with id: " + request.getRefereeId()));

            race.setReferee(referee);

            // khai
            if (!"CANCELLED".equals(race.getStatus() == null ? "" : race.getStatus().name())) {
                race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE);
            }
        }

        if (request.getRaceRulesId() != null) {
            com.swp.hrtms.hrtmsbe.entity.RaceFormat raceRules = raceFormatRepository.findById(request.getRaceRulesId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Race rules not found with id: " + request.getRaceRulesId()));
            race.setRaceRules(raceRules);
        }

        if (request.getExpectedDurationMinutes() != null) {
            race.setExpectedDurationMinutes(request.getExpectedDurationMinutes());
        }

        if (request.getBreakTimeMinutes() != null) {
            race.setBreakTimeMinutes(request.getBreakTimeMinutes());
        }

        if (request.getName() != null)
            race.setName(request.getName());
        if (request.getDate() != null)
            race.setDate(request.getDate());
        if (request.getStartTime() != null)
            race.setStartTime(request.getStartTime());
        if (request.getEndTime() != null)
            race.setEndTime(request.getEndTime());
        if (request.getDistanceM() != null)
            race.setDistanceM(request.getDistanceM());
        if (request.getNumHorse() != null)
            race.setNumHorse(request.getNumHorse());
        if (request.getStatus() != null && !com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE
                .equals(request.getStatus() == null ? "" : request.getStatus().name()))
            race.setStatus(request.getStatus());
        if (request.getReason() != null)
            race.setReason(request.getReason());
        if (request.getCanceledAt() != null)
            race.setCanceledAt(request.getCanceledAt());

        if (request.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Tournament not found with id: " + request.getTournamentId()));
            race.setTournament(tournament);
        }

        race = raceRepository.save(race);

        if (refereeChanged) {
            sendRefereeInvitation(race);
        }

        // Broadcast NEW_RACE if published
        if (oldStatus != com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED
                && race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED) {
            java.util.List<com.swp.hrtms.hrtmsbe.entity.User> targetUsers = userRepository
                    .findByRoleIn(java.util.Arrays.asList("JOCKEY", "HORSE_OWNER", "SPECTATOR"));
            if (!targetUsers.isEmpty()) {
                com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                        .builder()
                        .sender(null) // System notification
                        .title("New Race: " + race.getName())
                        .content("A new race has been published in the tournament.")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.NEW_RACE)
                        .race(race)
                        .build();
                notification = notificationRepository.save(notification);

                java.util.List<com.swp.hrtms.hrtmsbe.entity.NotificationRecipient> recipients = new ArrayList<>();
                for (com.swp.hrtms.hrtmsbe.entity.User user : targetUsers) {
                    com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                            .builder()
                            .notification(notification)
                            .recipient(user)
                            .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                            .build();
                    recipients.add(recipient);
                }
                notificationRecipientRepository.saveAll(recipients);
            }
        }

        return mapToRaceResponse(race);
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentRaceDetailsResponse getRaceDetailsByTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));

        List<Race> races = raceRepository.findByTournamentId(tournamentId);

        long totalEntries = 0;
        List<RaceDashboardItem> raceItems = new ArrayList<>();

        for (Race race : races) {
            RaceDashboardItem item = RaceDashboardItem.builder()
                    .id(race.getId())
                    .name(race.getName())
                    .date(race.getDate())
                    .startTime(race.getStartTime())
                    .endTime(race.getEndTime())
                    .status(race.getStatus())
                    .refereeId(race.getReferee() != null ? race.getReferee().getId() : null)
                    .refereeName(race.getReferee() != null ? race.getReferee().getName() : null)
                    .distanceM(race.getDistanceM())
                    .build();
            raceItems.add(item);
        }

        return TournamentRaceDetailsResponse.builder()
                .tournamentStatus(tournament.getStatus())
                .totalEntries(totalEntries)
                .races(raceItems)
                .build();
    }

    @Override
    @Transactional
    public String cancelRace(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceCancelRequest request) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        // khai
        if ("CANCELLED".equals(race.getStatus() == null ? "" : race.getStatus().name())) {
            throw new IllegalArgumentException("Race is already cancelled");
        }

        race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED);
        race.setReason(request.getReason());
        raceRepository.save(race);

        processRefunds(raceId);

        return "Race has been successfully cancelled.";
    }

    @Override
    @Transactional
    public String walkOverRace(Integer raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        // khai
        if ("CANCELLED".equals(race.getStatus() == null ? "" : race.getStatus().name())) {
            throw new IllegalArgumentException("Race is already cancelled");
        }

        if ("WALK_OVER".equals(race.getStatus() == null ? "" : race.getStatus().name())) {
            throw new IllegalArgumentException("Race is already WALK_OVER");
        }

        race.setStatus(RaceStatus.WALK_OVER);
        race.setReason("There is currently only one horse competing");
        raceRepository.save(race);

        processRefunds(raceId);

        return "Race has been successfully converted to WALK_OVER.";
    }

    private void processRefunds(Integer raceId) {
        List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);

        for (Prediction p : predictions) {
            if (isRefundablePrediction(p)) {
                refundPrediction(p);
            }
        }
    }

    private boolean isRefundablePrediction(Prediction prediction) {
        if (prediction == null || prediction.getStatus() == null) {
            return false;
        }
        return prediction.getStatus() == com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING
                || prediction.getStatus() == com.swp.hrtms.hrtmsbe.enums.PredictionStatus.LOCKED;
    }

    private void refundPrediction(Prediction prediction) {
        if (prediction == null || prediction.getStatus() == com.swp.hrtms.hrtmsbe.enums.PredictionStatus.CANCELLED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        prediction.setStatus(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.CANCELLED);
        predictionRepository.save(prediction);

        if (prediction.getSpectator() == null || prediction.getPointsInvested() == null) {
            return;
        }

        Wallet wallet = walletRepository.findByUser_Id(prediction.getSpectator().getId()).orElse(null);
        if (wallet == null) {
            return;
        }

        wallet.setBalance(wallet.getBalance() + prediction.getPointsInvested());
        wallet.setUpdatedAt(now);
        walletRepository.save(wallet);

        Transaction tx = Transaction.builder()
                .wallet(wallet)
                .race(prediction.getRace())
                .horse(prediction.getPredictedHorse())
                .amount(prediction.getPointsInvested())
                .type("PREDICTION_REFUND")
                .createdAt(now)
                .build();
        transactionRepository.save(tx);

        // create noti refund prediction for spectator
        User sender = prediction.getRace() != null
                && prediction.getRace().getTournament() != null
                        ? prediction.getRace().getTournament().getAdmin()
                        : null;
        String raceName = prediction.getRace() != null ? prediction.getRace().getName() : "the race";
        String horseName = prediction.getPredictedHorse() != null ? prediction.getPredictedHorse().getName()
                : "your selected horse";
        Notification notification = Notification.builder()
                .sender(sender)
                .race(prediction.getRace())
                .title("Prediction refund")
                .content("Your prediction for " + horseName + " in " + raceName + " has been refunded.")
                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.SYSTEM)
                .createdAt(now)
                .build();
        notification = notificationRepository.save(notification);

        NotificationRecipient recipient = NotificationRecipient.builder()
                .notification(notification)
                .recipient(prediction.getSpectator())
                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                .build();
        notificationRecipientRepository.save(recipient);
    }

    @Override
    @Transactional
    public String updateRaceTime(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceUpdateTimeRequest request) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        // khai
        if ("CANCELLED".equals(race.getStatus() == null ? "" : race.getStatus().name())
                || "COMPLETE".equals(race.getStatus() == null ? "" : race.getStatus().name())) {
            throw new IllegalArgumentException("Cannot update time for a cancelled or finished race.");
        }

        if (request.getDate() == null || request.getStartTime() == null || request.getEndTime() == null) {
            throw new IllegalArgumentException("Race date, start time, and end time are required.");
        }

        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time.");
        }

        // Check overlap in tournament excluding this race (BR_05: No scheduling
        // conflicts)
        if (raceRepository.existsOverlappingInTournamentExcludingRace(
                race.getTournament().getId(), race.getId(), request.getDate(), request.getStartTime(),
                request.getEndTime())) {
            throw new IllegalArgumentException("Updated time overlaps with another race in the tournament.");
        }

        // Check overlap for referee excluding this race
        if (race.getReferee() != null) {
            if (raceRepository.existsOverlappingForRefereeExcludingRace(
                    race.getReferee().getId(), race.getId(), request.getDate(), request.getStartTime(),
                    request.getEndTime())) {
                throw new IllegalArgumentException("Referee is already assigned to another overlapping race.");
            }
        }

        // Update fields
        race.setDate(request.getDate());
        race.setStartTime(request.getStartTime());
        race.setEndTime(request.getEndTime());

        raceRepository.save(race);

        return "Race time has been successfully updated.";
    }

    @Override
    public void predictScheduleUpdate() {
        // Tớ muốn cậu viết một hàm cập nhật lịch dự đoán nhưng để trống code ta sẽ phát
        // triễn chức năng đấy sau
        // TODO: Implement schedule prediction logic
    }

    /**
     * Gửi thông báo mời trọng tài tham gia điều hành cuộc đua.
     * Người gửi (sender) được thiết lập là Admin liên kết trực tiếp với Tournament
     * của cuộc đua.
     * Loại thông báo là
     * com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_INVITATION.
     *
     * @param race Cuộc đua được phân công trọng tài
     */
    private void sendRefereeInvitation(Race race) {
        if (race.getReferee() == null) {
            return;
        }

        // Lấy admin của tournament làm người gửi thông báo
        User sender = race.getTournament().getAdmin();

        // Tạo nội dung thông báo
        String title = "Race Referee Invitation";
        String content = String.format(
                "You are invited to referee the race '%s' in tournament '%s' on %s.",
                race.getName(),
                race.getTournament().getName(),
                race.getDate());

        // Khởi tạo thực thể Notification liên kết với cuộc đua
        Notification notification = Notification.builder()
                .sender(sender)
                .title(title)
                .content(content)
                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.REFEREE_INVITATION)
                .race(race)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // Khởi tạo thực thể NotificationRecipient để liên kết thông báo với Trọng tài
        // nhận
        NotificationRecipient recipient = NotificationRecipient.builder()
                .notification(notification)
                .recipient(race.getReferee())
                // khai
                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                .build();
        notificationRecipientRepository.save(recipient);
    }

    @Override
    @Transactional
    public RaceResponse lateScratch(Integer raceId, Integer horseId, String reason) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        if (race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED
                && race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.PREPARE) {
            throw new IllegalArgumentException("Can only late scratch a horse before the race starts.");
        }

        com.swp.hrtms.hrtmsbe.entity.RegistrationForm form = disqualifyRegistrationForm(raceId, horseId);
        removePlacementAndShiftRanks(raceId, form.getId());

        // 2. Cancel and Refund all predictions for this horse
        List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);
        for (Prediction p : predictions) {
            if (p.getPredictedHorse() != null && p.getPredictedHorse().getId().equals(horseId)
                    && isRefundablePrediction(p)) {
                refundPrediction(p);
            }
        }

        return mapToRaceResponse(race);
    }

    @Override
    @Transactional
    public RaceResponse disqualifyHorse(Integer raceId, Integer horseId, String reason) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        if (race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.ONGOING
                && race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.PREPARE
                && race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED) {
            throw new IllegalArgumentException("Can only disqualify a horse before race completion.");
        }

        com.swp.hrtms.hrtmsbe.entity.RegistrationForm form = disqualifyRegistrationForm(raceId, horseId);
        removePlacementAndShiftRanks(raceId, form.getId());

        List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);
        for (Prediction p : predictions) {
            if (p.getPredictedHorse() != null && p.getPredictedHorse().getId().equals(horseId)
                    && isRefundablePrediction(p)) {
                refundPrediction(p);
            }
        }

        return mapToRaceResponse(race);
    }

    private com.swp.hrtms.hrtmsbe.entity.RegistrationForm disqualifyRegistrationForm(Integer raceId, Integer horseId) {
        com.swp.hrtms.hrtmsbe.entity.RegistrationForm form = registrationFormRepository.findByRace_Id(raceId).stream()
                .filter(f -> f.getHorse() != null && f.getHorse().getId().equals(horseId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Horse is not registered in this race."));

        form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED);
        return registrationFormRepository.save(form);
    }

    private void removePlacementAndShiftRanks(Integer raceId, Integer registrationFormId) {
        com.swp.hrtms.hrtmsbe.entity.RaceResult raceResult = raceResultRepository.findByRace_Id(raceId).orElse(null);
        if (raceResult == null) {
            return;
        }

        List<com.swp.hrtms.hrtmsbe.entity.RacePlacement> placements =
                racePlacementRepository.findByRaceResult_Id(raceResult.getId());
        com.swp.hrtms.hrtmsbe.entity.RacePlacement disqualifiedPlacement = placements.stream()
                .filter(p -> p.getRegistrationForm() != null
                        && p.getRegistrationForm().getId().equals(registrationFormId))
                .findFirst()
                .orElse(null);

        if (disqualifiedPlacement == null) {
            return;
        }

        Integer removedPosition = disqualifiedPlacement.getFinishPosition();
        racePlacementRepository.delete(disqualifiedPlacement);

        if (removedPosition == null) {
            return;
        }

        for (com.swp.hrtms.hrtmsbe.entity.RacePlacement placement : placements) {
            if (placement.getId().equals(disqualifiedPlacement.getId())
                    || placement.getFinishPosition() == null
                    || placement.getFinishPosition() <= removedPosition) {
                continue;
            }
            placement.setFinishPosition(placement.getFinishPosition() - 1);
            racePlacementRepository.save(placement);
        }
    }

    @Override
    @Transactional
    public RaceResponse startRace(Integer raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        if (race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED
                && race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.PREPARE) {
            throw new IllegalArgumentException("Race must be PUBLISHED or PREPARE to start.");
        }

        race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.ONGOING);
        raceRepository.save(race);

        // Lock all predictions for this race
        List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);
        for (Prediction p : predictions) {
            if (p.getStatus() == com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING) {
                p.setStatus(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.LOCKED);
                predictionRepository.save(p);
            }
        }

        return mapToRaceResponse(race);
    }

}
