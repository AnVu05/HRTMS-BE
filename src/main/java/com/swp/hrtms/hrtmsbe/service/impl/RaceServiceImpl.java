package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.RaceBatchCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RaceCreateRequest;
//Khai
import com.swp.hrtms.hrtmsbe.dto.request.RacePrizeRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceDashboardItem;
import com.swp.hrtms.hrtmsbe.dto.response.RacePrizeResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResponse;
//Khai
import com.swp.hrtms.hrtmsbe.dto.response.SingleRaceCreateResponse;
import com.swp.hrtms.hrtmsbe.dto.response.TournamentRaceDetailsResponse;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.RacePrize;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.Prediction;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.service.RaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.repository.PredictionRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import com.swp.hrtms.hrtmsbe.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RaceServiceImpl implements RaceService {

    private final RaceRepository raceRepository;
    private final TournamentRepository tournamentRepository;
    private final RefereeRepository refereeRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository;
    private final com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository registrationFormRepository;
    private final com.swp.hrtms.hrtmsbe.repository.PredictionRepository predictionRepository;
    private final com.swp.hrtms.hrtmsbe.repository.WalletRepository walletRepository;
    private final com.swp.hrtms.hrtmsbe.repository.TransactionRepository transactionRepository;

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

            // 1. Validation: Overlap in DB (Tournament)
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

                // 3. Validation: Overlap in DB (Referee)
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

            Race race = Race.builder()
                    .tournament(tournament)
                    .name(raceReq.getName())
                    .date(raceReq.getDate())
                    .startTime(raceReq.getStartTime())
                    .endTime(raceReq.getEndTime())
                    // .laps(raceReq.getLaps())
                    // .numHorse(raceReq.getNumHorse())
                    .referee(referee)
                    .status(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE)
                    .track(raceReq.getTrack()) // Ánh xạ trường track địa điểm thi đấu
                    .build();

            racesToSave.add(race);
        }

        racesToSave = raceRepository.saveAll(racesToSave);

        List<RaceResponse> responses = new ArrayList<>();
        for (Race r : racesToSave) {
            // Gửi thông báo lời mời cho trọng tài nếu được phân công trong đợt tạo hàng
            // loạt
            sendRefereeInvitation(r);

            responses.add(RaceResponse.builder()
                    .id(r.getId())
                    .tournamentId(r.getTournament().getId())
                    .name(r.getName())
                    .date(r.getDate())
                    .startTime(r.getStartTime())
                    .endTime(r.getEndTime())
                    // .laps(r.getLaps())
                    // .numHorse(r.getNumHorse())
                    .refereeId(r.getReferee() != null ? r.getReferee().getId() : null)
                    .status(r.getStatus())
                    .track(r.getTrack()) // Ánh xạ trường track trả về cho client
                    .build());
        }

        return responses;
    }

    @Override
    @Transactional
    // Khai
    public RaceResponse createSingleRace(
            com.swp.hrtms.hrtmsbe.dto.request.RaceRequest request) {
        if (request.getTournamentId() == null || request.getDate() == null ||
                request.getStartTime() == null || request.getEndTime() == null ||
                request.getName() == null || request.getName().isBlank() ||
                request.getDistanceM() == null || request.getHorseBreed() == null ||
                request.getHorseBreed().isBlank() || request.getWeightKg() == null ||
                request.getHorseAge() == null) {
            throw new IllegalArgumentException(
                    "Tournament ID, race name, date, distance, start time, end time, horse breed, weight, and horse age are required.");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException(
                    "Start time must be before end time for race '" + request.getName() + "'.");
        }

        if (request.getDistanceM() <= 0) {
            throw new IllegalArgumentException("Distance must be greater than 0 meters.");
        }

        if (request.getWeightKg().signum() <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0 kilograms.");
        }

        if (request.getHorseAge() <= 0) {
            throw new IllegalArgumentException("Horse age must be greater than 0 years.");
        }

        if (request.getBettingReward() != null && request.getBettingReward() < 0) {
            throw new IllegalArgumentException("Betting reward cannot be negative.");
        }

        List<RacePrizeRequest> requestedPrizes = request.getJockeyPrizes() == null
                ? List.of()
                : request.getJockeyPrizes();
        Set<Integer> receivedRanks = new HashSet<>();

        for (RacePrizeRequest prize : requestedPrizes) {
            if (prize == null || prize.getRank() == null || prize.getAmount() == null) {
                throw new IllegalArgumentException("Each jockey prize must include rank and amount.");
            }
            if (prize.getRank() < 1 || prize.getRank() > 3) {
                throw new IllegalArgumentException("Jockey prize rank must be between 1 and 3.");
            }
            if (!receivedRanks.add(prize.getRank())) {
                throw new IllegalArgumentException("Jockey prize ranks cannot be duplicated.");
            }
            if (prize.getAmount() < 0) {
                throw new IllegalArgumentException("Jockey prize amount cannot be negative.");
            }
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

        Race race = Race.builder()
                .tournament(tournament)
                .name(request.getName())
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .distanceM(request.getDistanceM())
                .horseBreed(request.getHorseBreed() != null ? request.getHorseBreed().trim() : null)
                .weightKg(request.getWeightKg())
                .horseAge(request.getHorseAge())
                .bettingReward(request.getBettingReward() == null ? 0L : request.getBettingReward())
                .referee(referee)
                .status(request.getStatus() != null ? request.getStatus()
                        : com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE)
                .reason(request.getReason())
                .expectedDurationMinutes(request.getExpectedDurationMinutes())
                .breakTimeMinutes(request.getBreakTimeMinutes())
                .canceledAt(request.getCanceledAt())
                .build();

        for (int rank = 1; rank <= 3; rank++) {
            final int currentRank = rank;
            Long amount = requestedPrizes.stream()
                    .filter(prize -> prize.getRank() == currentRank)
                    .map(RacePrizeRequest::getAmount)
                    .findFirst()
                    .orElse(0L);

            race.getJockeyPrizes().add(RacePrize.builder()
                    .race(race)
                    .rank(rank)
                    .amount(amount)
                    .build());
        }

        race = raceRepository.save(race);

        sendRefereeInvitation(race);

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
                .horseBreed(race.getHorseBreed())
                .weightKg(race.getWeightKg())
                .horseAge(race.getHorseAge())
                .bettingReward(race.getBettingReward())
                .jockeyPrizes(race.getJockeyPrizes().stream()
                        .sorted(Comparator.comparing(RacePrize::getRank))
                        .map(prize -> RacePrizeResponse.builder()
                                .rank(prize.getRank())
                                .amount(prize.getAmount())
                                .build())
                        .toList())
                .refereeId(race.getReferee() != null ? race.getReferee().getId() : null)
                .status(race.getStatus())
                .track(race.getTrack())
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
                .orElseThrow(() -> new RuntimeException("Race not found"));

        boolean refereeChanged = false;
        boolean timeChanged = false;

        if (request.getRefereeId() != null) {
            if (race.getReferee() == null || !race.getReferee().getId().equals(request.getRefereeId())) {
                refereeChanged = true;
            }

            Referee referee = refereeRepository.findById(request.getRefereeId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Referee not found with id: " + request.getRefereeId()));

            race.setReferee(referee);

            // khai
            if (!"CANCEL".equals(race.getStatus() == null ? "" : race.getStatus().name())) {
                race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE);
            }
        }

        // if (request.getRaceRulesId() != null) {
        // com.swp.hrtms.hrtmsbe.entity.RaceFormat raceRules =
        // raceFormatRepository.findById(request.getRaceRulesId())
        // .orElseThrow(() -> new IllegalArgumentException(
        // "Race rules not found with id: " + request.getRaceRulesId()));
        // race.setRaceRules(raceRules);
        // }

        if (request.getExpectedDurationMinutes() != null) {
            race.setExpectedDurationMinutes(request.getExpectedDurationMinutes());
        }

        if (request.getBreakTimeMinutes() != null) {
            race.setBreakTimeMinutes(request.getBreakTimeMinutes());
        }

        if (request.getName() != null)
            race.setName(request.getName());
        if (request.getDate() != null && !request.getDate().equals(race.getDate())) {
            race.setDate(request.getDate());
            timeChanged = true;
        }
        if (request.getStartTime() != null && !request.getStartTime().equals(race.getStartTime())) {
            race.setStartTime(request.getStartTime());
            timeChanged = true;
        }
        if (request.getEndTime() != null && !request.getEndTime().equals(race.getEndTime())) {
            race.setEndTime(request.getEndTime());
            timeChanged = true;
        }
        if (request.getDistanceM() != null)
            race.setDistanceM(request.getDistanceM());
        if (request.getHorseBreed() != null)
            race.setHorseBreed(request.getHorseBreed());
        if (request.getWeightKg() != null)
            race.setWeightKg(request.getWeightKg());
        if (request.getHorseAge() != null)
            race.setHorseAge(request.getHorseAge());
        if (request.getBettingReward() != null)
            race.setBettingReward(request.getBettingReward());
        if (request.getStatus() != null && !com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE
                .equals(request.getStatus() == null ? "" : request.getStatus().name()))
            race.setStatus(request.getStatus());
        if (request.getReason() != null)
            race.setReason(request.getReason());
        if (request.getCanceledAt() != null)
            race.setCanceledAt(request.getCanceledAt());

        if (request.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                    .orElseThrow(() -> new IllegalArgumentException("Tournament not found"));
            race.setTournament(tournament);
        }

        race = raceRepository.save(race);

        if (refereeChanged) {
            sendRefereeInvitation(race);
        }

        if (timeChanged) {
            predictScheduleUpdate(race);
        }

        return mapToRaceResponse(race);
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentRaceDetailsResponse getRaceDetailsByTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<Race> races = raceRepository.findByTournamentId(tournamentId);
        List<RaceDashboardItem> raceItems = new ArrayList<>();
        long totalEntries = 0;
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
                    .track(race.getTrack())
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
                .orElseThrow(() -> new RuntimeException("Race not found"));

        // khai
        if ("CANCEL".equals(race.getStatus() == null ? "" : race.getStatus().name())) {
            throw new IllegalArgumentException("Race is already cancelled");
        }

        race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED);
        race.setReason(request.getReason());
        raceRepository.save(race);

        processRefunds(raceId);

        return "Race has been successfully cancelled.";
    }

    private void processRefunds(Integer raceId) {
        List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);
        LocalDateTime now = LocalDateTime.now();

        for (Prediction p : predictions) {
            if ("PENDING".equalsIgnoreCase(p.getStatus() == null ? "" : p.getStatus().name())) {
                // khai
                p.setStatus(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.CANCELLED);
                predictionRepository.save(p);

                Wallet wallet = walletRepository.findByUserId(p.getSpectator().getId()).orElse(null);
                if (wallet != null) {
                    wallet.setBalance(wallet.getBalance() + p.getPointsInvested());
                    wallet.setUpdatedAt(now);
                    walletRepository.save(wallet);

                    Transaction tx = Transaction.builder()
                            .wallet(wallet)
                            .tournament(p.getRace() != null ? p.getRace().getTournament() : null)
                            .race(p.getRace())
                            .horse(p.getPredictedHorse())
                            .amount(p.getPointsInvested())
                            .type("PREDICTION_REFUND")
                            .createdAt(now)
                            .build();
                    transactionRepository.save(tx);

                    // Gửi thông báo hoàn tiền
                    com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                            .builder()
                            .title("Prediction Points Refund")
                            .content("Race " + p.getRace().getName() + " has been cancelled. You have been refunded "
                                    + p.getPointsInvested() + " points to your wallet.")
                            .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.REFUND)
                            .race(p.getRace())
                            .createdAt(now)
                            .build();
                    notificationRepository.save(notification);

                    com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                            .builder()
                            .notification(notification)
                            .recipient(p.getSpectator())
                            .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                            .build();
                    notificationRecipientRepository.save(recipient);
                }
            }
        }
    }

    @Override
    @Transactional
    public String updateRaceTime(Integer raceId, com.swp.hrtms.hrtmsbe.dto.request.RaceUpdateTimeRequest request) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new RuntimeException("Race not found"));

        // khai
        if ("CANCEL".equals(race.getStatus() == null ? "" : race.getStatus().name())
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

        predictScheduleUpdate(race);

        return "Race time has been successfully updated.";
    }

    @Override
    public void predictScheduleUpdate(Race race) {
        LocalDateTime now = LocalDateTime.now();
        List<com.swp.hrtms.hrtmsbe.entity.NotificationRecipient> recipients = new ArrayList<>();

        com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                .title("Race Time Updated")
                .content("Race " + race.getName() + " has been rescheduled to " + race.getDate() + " at "
                        + race.getStartTime() + ". "
                        + "Due to an update to the race schedule, the predicted times have been updated.")
                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.RACE_UPDATE)
                .race(race)
                .createdAt(now)
                .build();
        notificationRepository.save(notification);

        // 1. All Spectators in the system
        List<com.swp.hrtms.hrtmsbe.entity.User> spectators = userRepository.findByRoleIn(List.of("SPECTATOR"));
        for (com.swp.hrtms.hrtmsbe.entity.User spectator : spectators) {
            recipients.add(com.swp.hrtms.hrtmsbe.entity.NotificationRecipient.builder()
                    .notification(notification)
                    .recipient(spectator)
                    .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                    .build());
        }

        // 2. Horse Owners and Jockeys
        List<com.swp.hrtms.hrtmsbe.entity.RegistrationForm> forms = registrationFormRepository
                .findByRace_Id(race.getId());
        for (com.swp.hrtms.hrtmsbe.entity.RegistrationForm form : forms) {
            if (form.getOwner() != null && form.getOwner().getUser() != null) {
                recipients.add(com.swp.hrtms.hrtmsbe.entity.NotificationRecipient.builder()
                        .notification(notification)
                        .recipient(form.getOwner().getUser())
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build());
            }
            if (form.getJockey() != null) {
                recipients.add(com.swp.hrtms.hrtmsbe.entity.NotificationRecipient.builder()
                        .notification(notification)
                        .recipient(form.getJockey())
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build());
            }
        }

        // 3. Referee
        if (race.getReferee() != null) {
            recipients.add(com.swp.hrtms.hrtmsbe.entity.NotificationRecipient.builder()
                    .notification(notification)
                    .recipient(race.getReferee())
                    .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                    .build());
        }

        // Unique recipients
        java.util.Map<Integer, com.swp.hrtms.hrtmsbe.entity.NotificationRecipient> uniqueRecipients = new java.util.HashMap<>();
        for (com.swp.hrtms.hrtmsbe.entity.NotificationRecipient r : recipients) {
            uniqueRecipients.put(r.getRecipient().getId(), r);
        }

        notificationRecipientRepository.saveAll(uniqueRecipients.values());
    }

    /**
     * Gửi thông báo mời trọng tài tham gia điều hành cuộc đua.
     * Người gửi (sender) được thiết lập là Admin liên kết trực tiếp với Tournament
     * của cuộc đua.
     * Loại thông báo là "REFEREE_INVITATION".
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
                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                .build();
        notificationRecipientRepository.save(recipient);
    }

    @Override
    @Transactional(readOnly = true)
    public RaceResponse getRaceById(Integer id) {
        Race race = raceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Race not found with id: " + id));
        return mapToRaceResponse(race);
    }
}
