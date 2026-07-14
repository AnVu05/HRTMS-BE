package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.entity.RaceResult;
import com.swp.hrtms.hrtmsbe.entity.RacePlacement;
import com.swp.hrtms.hrtmsbe.entity.Prediction;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceResultRepository;
import com.swp.hrtms.hrtmsbe.repository.RacePlacementRepository;
import com.swp.hrtms.hrtmsbe.repository.PredictionRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import com.swp.hrtms.hrtmsbe.repository.TransactionRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.service.RaceResultService;
import com.swp.hrtms.hrtmsbe.dto.request.RaceResultRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResultResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RaceResultServiceImpl implements RaceResultService {

    private final RaceResultRepository raceResultRepository;
    private final RacePlacementRepository racePlacementRepository;
    private final PredictionRepository predictionRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final RaceRepository raceRepository;
    private final RegistrationFormRepository registrationFormRepository;
    private final TournamentRepository tournamentRepository;
    private final RefereeRepository refereeRepository;
    private final HorseRepository horseRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    public RaceResultServiceImpl(RaceResultRepository raceResultRepository,
                                 RacePlacementRepository racePlacementRepository,
                                 PredictionRepository predictionRepository,
                                 WalletRepository walletRepository,
                                 TransactionRepository transactionRepository,
                                 RaceRepository raceRepository,
                                 RegistrationFormRepository registrationFormRepository,
                                 TournamentRepository tournamentRepository,
                                 RefereeRepository refereeRepository,
                                 HorseRepository horseRepository,
                                 NotificationRepository notificationRepository,
                                 NotificationRecipientRepository notificationRecipientRepository) {
        this.raceResultRepository = raceResultRepository;
        this.racePlacementRepository = racePlacementRepository;
        this.predictionRepository = predictionRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.raceRepository = raceRepository;
        this.registrationFormRepository = registrationFormRepository;
        this.tournamentRepository = tournamentRepository;
        this.refereeRepository = refereeRepository;
        this.horseRepository = horseRepository;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    @Override
    @Transactional
    public RaceResultResponse create(RaceResultRequest request) {
        com.swp.hrtms.hrtmsbe.enums.RaceResultStatus status = request.getStatus() != null
                ? request.getStatus()
                : com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.TEMPORARY;
        validateOfficialEvidence(status, request.getPhotoFinishImage());

        RaceResult result = RaceResult.builder()
                .race(findRaceOrNull(request.getRaceId()))
                .referee(findRefereeOrNull(request.getRefereeId()))
                .status(status)
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now())
                .photoFinishImage(request.getPhotoFinishImage())
                .build();
        validateRaceAcceptsManualResult(result.getRace());
        
        result = raceResultRepository.save(result);

        if (request.getPlacements() != null && !request.getPlacements().isEmpty()) {
            for (com.swp.hrtms.hrtmsbe.dto.request.RacePlacementRequest pr : request.getPlacements()) {
                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form = null;
                if (pr.getRegistrationFormId() != null) {
                    form = registrationFormRepository.findById(pr.getRegistrationFormId()).orElse(null);
                }
                com.swp.hrtms.hrtmsbe.entity.RacePlacement placement = com.swp.hrtms.hrtmsbe.entity.RacePlacement.builder()
                        .raceResult(result)
                        .registrationForm(form)
                        .finishPosition(pr.getFinishPosition())
                        .finishTime(pr.getFinishTime())
                        .weighInWeight(pr.getWeighInWeight())
                        .build();
                racePlacementRepository.save(placement);
            }
        }

        //khai
        if (status == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL) {
            processRewards(result.getId(), request.getRaceId());
        }

        return toResponse(result);
    }

    @Override
    @Transactional
    public RaceResultResponse update(Integer id, RaceResultRequest request) {
        RaceResult result = raceResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RaceResult not found with id: " + id));
        
        if (result.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL) {
            throw new IllegalArgumentException("Official race result cannot be updated.");
        }

        String photoFinishImage = request.getPhotoFinishImage() != null
                ? request.getPhotoFinishImage()
                : result.getPhotoFinishImage();
        com.swp.hrtms.hrtmsbe.enums.RaceResultStatus status = request.getStatus() != null
                ? request.getStatus()
                : result.getStatus();
        validateOfficialEvidence(status, photoFinishImage);

        if (request.getRaceId() != null) {
            result.setRace(findRaceOrNull(request.getRaceId()));
        }
        validateRaceAcceptsManualResult(result.getRace());
        if (request.getRefereeId() != null) {
            result.setReferee(findRefereeOrNull(request.getRefereeId()));
        }
        result.setStatus(status);
        result.setPhotoFinishImage(photoFinishImage);
        if (request.getCreatedAt() != null) {
            result.setCreatedAt(request.getCreatedAt());
        }
        result = raceResultRepository.save(result);

        if (request.getPlacements() != null && !request.getPlacements().isEmpty()) {
            // Xóa các placements cũ
            java.util.List<com.swp.hrtms.hrtmsbe.entity.RacePlacement> oldPlacements = racePlacementRepository.findByRaceResult_Id(result.getId());
            racePlacementRepository.deleteAll(oldPlacements);
            
            // Lưu các placements mới
            for (com.swp.hrtms.hrtmsbe.dto.request.RacePlacementRequest pr : request.getPlacements()) {
                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form = null;
                if (pr.getRegistrationFormId() != null) {
                    form = registrationFormRepository.findById(pr.getRegistrationFormId()).orElse(null);
                }
                com.swp.hrtms.hrtmsbe.entity.RacePlacement placement = com.swp.hrtms.hrtmsbe.entity.RacePlacement.builder()
                        .raceResult(result)
                        .registrationForm(form)
                        .finishPosition(pr.getFinishPosition())
                        .finishTime(pr.getFinishTime())
                        .weighInWeight(pr.getWeighInWeight())
                        .build();
                racePlacementRepository.save(placement);
            }
        }

        // If changed to OFFICIAL, process rewards
        if (status == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL) {
            Integer raceId = result.getRace() != null ? result.getRace().getId() : null;
            processRewards(result.getId(), raceId);
        }

        return toResponse(result);
    }

    private void processRewards(Integer raceResultId, Integer raceId) {
        Race race = raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));

        List<RacePlacement> placements = racePlacementRepository.findByRaceResult_Id(raceResultId);
        Set<Integer> winningHorseIds = placements.stream()
                .filter(p -> p.getFinishPosition() != null && p.getFinishPosition() == 1)
                .map(RacePlacement::getRegistrationForm)
                .filter(form -> form != null && form.getHorse() != null)
                .map(form -> form.getHorse().getId())
                .collect(Collectors.toCollection(HashSet::new));

        List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);
        for (Prediction p : predictions) {
            if (p.getStatus() != com.swp.hrtms.hrtmsbe.enums.PredictionStatus.LOCKED) {
                continue;
            }

            Integer predictedHorseId = p.getPredictedHorse() != null ? p.getPredictedHorse().getId() : null;
            boolean winner = predictedHorseId != null && winningHorseIds.contains(predictedHorseId);
            Integer reward = 0;

            if (winner) {
                reward = p.getPointsInvested() * 2;
                Integer spectatorUserId = p.getSpectator() != null ? p.getSpectator().getId() : null;
                Wallet wallet = spectatorUserId != null ? walletRepository.findByUser_Id(spectatorUserId).orElse(null) : null;
                if (wallet != null) {
                    wallet.setBalance(wallet.getBalance() + reward);
                    wallet.setUpdatedAt(LocalDateTime.now());
                    walletRepository.save(wallet);

                    Transaction tx = Transaction.builder()
                            .wallet(wallet)
                            .race(race)
                            .horse(horseRepository.findById(predictedHorseId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Horse not found with id: " + predictedHorseId)))
                            .amount(reward)
                            .type("PREDICTION_REWARD")
                            .createdAt(LocalDateTime.now())
                            .build();
                    transactionRepository.save(tx);
                }
            }

            p.setStatus(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.DONE);
            predictionRepository.save(p);
            notifyPredictionResult(p, winner, reward);
        }
        
        // Cập nhật trạng thái các đội đua (RegistrationForm) thành COMPLETE
        List<RegistrationForm> forms = registrationFormRepository.findByRace_Id(raceId);
        for (RegistrationForm form : forms) {
            if (form.getStatus() == com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED
                    || form.getStatus() == com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DELETE) {
                continue;
            }
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.COMPLETE);
        }
        registrationFormRepository.saveAll(forms);

        // Also update Race status
        if (race != null) {
            //khai
            race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.COMPLETE);
            raceRepository.save(race);

            // Kiểm tra và cập nhật Tournament thành COMPLETE nếu tất cả các Race đều COMPLETE
            if (race.getTournament() != null) {
                com.swp.hrtms.hrtmsbe.entity.Tournament tournament = race.getTournament();
                boolean allRacesComplete = raceRepository.findByTournamentId(tournament.getId()).stream()
                        .allMatch(r -> r.getId().equals(raceId) || r.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.COMPLETE);
                
                // Cũng cần kiểm tra thêm điều kiện ngày kết thúc đã qua chưa (như mô tả của người dùng)
                if (allRacesComplete && java.time.LocalDate.now().isAfter(tournament.getEndDate())) {
                    tournament.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.COMPLETE);
                    tournamentRepository.save(tournament);
                }
            }
        }
    }

    private void notifyPredictionResult(Prediction prediction, boolean winner, Integer reward) {
        if (prediction.getSpectator() == null) {
            return;
        }

        Race race = prediction.getRace();
        String raceName = race != null ? race.getName() : "the race";
        String horseName = prediction.getPredictedHorse() != null ? prediction.getPredictedHorse().getName() : "your selected horse";
        String title = winner ? "Prediction won" : "Prediction lost";
        String content = winner
                ? "Nice pick! " + horseName + " won in " + raceName + ". +" + reward + " points."
                : "Your pick did not win in " + raceName + ".";

        Notification notification = Notification.builder()
                .sender(race != null && race.getTournament() != null ? race.getTournament().getAdmin() : null)
                .race(race)
                .title(title)
                .content(content)
                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.PREDICTED)
                .createdAt(LocalDateTime.now())
                .build();
        notification = notificationRepository.save(notification);

        NotificationRecipient recipient = NotificationRecipient.builder()
                .notification(notification)
                .recipient(prediction.getSpectator())
                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                .build();
        notificationRecipientRepository.save(recipient);
    }

    private void validateOfficialEvidence(com.swp.hrtms.hrtmsbe.enums.RaceResultStatus status, String photoFinishImage) {
        if (status == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL
                && (photoFinishImage == null || photoFinishImage.isBlank())) {
            throw new IllegalArgumentException("Photo-finish image is required before marking race result as official.");
        }
    }

    // Prevents referee-entered results for races that are closed by health-check walk-over or cancellation.
    private void validateRaceAcceptsManualResult(Race race) {
        if (race == null || race.getStatus() == null) {
            return;
        }
        if (race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED) {
            throw new IllegalArgumentException("Race result cannot be created for a cancelled race.");
        }
        if (race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.WALK_OVER) {
            throw new IllegalArgumentException(
                    "Race result cannot be entered manually for a walk-over race. It is finalized automatically after the remaining horse passes health check.");
        }
    }

    @Override
    public List<RaceResultResponse> getAll() {
        return raceResultRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RaceResultResponse getById(Integer id) {
        RaceResult result = raceResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RaceResult not found with id: " + id));
        return toResponse(result);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        RaceResult result = raceResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RaceResult not found with id: " + id));
        //khai
        if (result.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL) {
            throw new IllegalArgumentException("Official race result cannot be deleted.");
        }

        raceResultRepository.delete(result);
    }

    private Race findRaceOrNull(Integer raceId) {
        if (raceId == null) {
            return null;
        }
        return raceRepository.findById(raceId)
                .orElseThrow(() -> new ResourceNotFoundException("Race not found with id: " + raceId));
    }

    private Referee findRefereeOrNull(Integer refereeId) {
        if (refereeId == null) {
            return null;
        }
        return refereeRepository.findById(refereeId)
                .orElseThrow(() -> new ResourceNotFoundException("Referee not found with id: " + refereeId));
    }

    private RaceResultResponse toResponse(RaceResult result) {
        return RaceResultResponse.builder()
                .id(result.getId())
                .raceId(result.getRace() != null ? result.getRace().getId() : null)
                .refereeId(result.getReferee() != null ? result.getReferee().getId() : null)
                .status(result.getStatus())
                .createdAt(result.getCreatedAt())
                .photoFinishImage(result.getPhotoFinishImage())
                .build();
    }
}

