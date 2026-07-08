package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.entity.Prediction;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.enums.RaceStatus;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
import com.swp.hrtms.hrtmsbe.repository.PredictionRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.SpectatorRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import com.swp.hrtms.hrtmsbe.repository.TransactionRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.service.PredictionService;
import com.swp.hrtms.hrtmsbe.dto.request.PredictionRequest;
import com.swp.hrtms.hrtmsbe.dto.response.PredictionResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionServiceImpl implements PredictionService {
        private final PredictionRepository predictionRepository;
        private final RaceRepository raceRepository;
        private final WalletRepository walletRepository;
        private final TransactionRepository transactionRepository;
        private final SpectatorRepository spectatorRepository;
        private final HorseRepository horseRepository;
        private final NotificationRepository notificationRepository;
        private final NotificationRecipientRepository notificationRecipientRepository;

        public PredictionServiceImpl(PredictionRepository predictionRepository,
                        RaceRepository raceRepository,
                        WalletRepository walletRepository,
                        TransactionRepository transactionRepository,
                        SpectatorRepository spectatorRepository,
                        HorseRepository horseRepository,
                        NotificationRepository notificationRepository,
                        NotificationRecipientRepository notificationRecipientRepository) {
                this.predictionRepository = predictionRepository;
                this.raceRepository = raceRepository;
                this.walletRepository = walletRepository;
                this.transactionRepository = transactionRepository;
                this.spectatorRepository = spectatorRepository;
                this.horseRepository = horseRepository;
                this.notificationRepository = notificationRepository;
                this.notificationRecipientRepository = notificationRecipientRepository;
        }

        @Override
        @Transactional
        public PredictionResponse create(PredictionRequest request) {
                Race race = raceRepository.findById(request.getRaceId())
                                .orElseThrow(() -> new IllegalArgumentException("Race not found."));

                if (race.getStatus() == RaceStatus.WALK_OVER || race.getStatus() == RaceStatus.CANCELLED) {
                        throw new IllegalArgumentException("Race is already cancelled or walkover.");
                }
                // BR_09: Predicted Time (Only enabled 1 hour beforehand and locks when race
                // begins)
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime raceStartDateTime = LocalDateTime.of(race.getDate(), race.getStartTime());
                if (now.isBefore(raceStartDateTime.minusHours(1)) || now.isAfter(raceStartDateTime)) {
                        throw new IllegalArgumentException(
                                        "Predictions are only allowed within 1 hour before the race starts.");
                }

                // BR_10: Ticket Limit (1 prediction per spectator and race)
                if (predictionRepository.existsBySpectator_IdAndRace_Id(request.getSpectatorId(),
                                request.getRaceId())) {
                        throw new IllegalArgumentException("You have already made a prediction for this race.");
                }

                // BR_16: Deduct points
                Wallet wallet = walletRepository.findByUser_Id(request.getSpectatorId())
                                .orElseThrow(() -> new IllegalArgumentException("Wallet not found."));

                if (wallet.getBalance() < request.getPointsInvested()) {
                        throw new IllegalArgumentException("Insufficient points in wallet.");
                }

                // Deduct points
                wallet.setBalance(wallet.getBalance() - request.getPointsInvested());
                wallet.setUpdatedAt(now);
                walletRepository.save(wallet);

                // Save transaction
                Transaction tx = Transaction.builder()
                                .wallet(wallet)
                                .tournament(race.getTournament())
                                .race(race)
                                .horse(request.getPredictedHorseId() != null
                                                ? horseRepository.getReferenceById(request.getPredictedHorseId())
                                                : null)
                                .amount(-request.getPointsInvested())
                                .type("PREDICTION_DEDUCT")
                                .createdAt(now)
                                .build();
                transactionRepository.save(tx);

                Prediction prediction = Prediction.builder()
                                .spectator(request.getSpectatorId() != null
                                                ? spectatorRepository.getReferenceById(request.getSpectatorId())
                                                : null)
                                .race(race)
                                .predictedHorse(request.getPredictedHorseId() != null
                                                ? horseRepository.getReferenceById(request.getPredictedHorseId())
                                                : null)
                                .pointsInvested(request.getPointsInvested())
                                // khai
                                .status(request.getStatus() != null ? request.getStatus()
                                                : com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING)
                                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : now)
                                .build();

                prediction = predictionRepository.save(prediction);

                com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                                .builder()
                                .title("The prediction was successful!")
                                .content("You have successfully participated in predicting the race " + race.getName()
                                                + " with " + request.getPointsInvested() + " points.")
                                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.PREDICTED)
                                .race(race)
                                .createdAt(now)
                                .build();
                notification = notificationRepository.save(notification);

                if (prediction.getSpectator() != null) {
                        com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                                        .builder()
                                        .notification(notification)
                                        .recipient(prediction.getSpectator())
                                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                                        .build();
                        notificationRecipientRepository.save(recipient);
                }

                return toResponse(prediction);
        }

        @Override
        public List<PredictionResponse> getAll() {
                // khai
                return predictionRepository.findAll().stream()
                                .filter(prediction -> !isDeleted(prediction))
                                .map(this::toResponse)
                                .collect(Collectors.toList());
        }

        private PredictionResponse toResponse(Prediction prediction) {
                return PredictionResponse.builder()
                                .id(prediction.getId())
                                .spectatorId(prediction.getSpectator() != null ? prediction.getSpectator().getId()
                                                : null)
                                .raceId(prediction.getRace() != null ? prediction.getRace().getId() : null)
                                .predictedHorseId(
                                                prediction.getPredictedHorse() != null
                                                                ? prediction.getPredictedHorse().getId()
                                                                : null)
                                .pointsInvested(prediction.getPointsInvested())
                                .status(prediction.getStatus())
                                .createdAt(prediction.getCreatedAt())
                                .build();
        }

        private boolean isDeleted(Prediction prediction) {
                return "CANCELLED"
                                .equalsIgnoreCase(prediction.getStatus() == null ? "" : prediction.getStatus().name());
        }
}
