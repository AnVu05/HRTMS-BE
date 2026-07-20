package com.swp.hrtms.hrtmsbe.service.impl;

// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.Prediction;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.enums.RaceStatus;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.repository.PredictionRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import com.swp.hrtms.hrtmsbe.repository.TransactionRepository;
import com.swp.hrtms.hrtmsbe.repository.SpectatorRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
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
    private final RegistrationFormRepository registrationFormRepository;

    public PredictionServiceImpl(PredictionRepository predictionRepository,
            RaceRepository raceRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            SpectatorRepository spectatorRepository,
            HorseRepository horseRepository,
            RegistrationFormRepository registrationFormRepository) {
        this.predictionRepository = predictionRepository;
        this.raceRepository = raceRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.spectatorRepository = spectatorRepository;
        this.horseRepository = horseRepository;
        this.registrationFormRepository = registrationFormRepository;
    }

    @Override
    @Transactional
    public PredictionResponse create(PredictionRequest request) {
        Race race = raceRepository.findById(request.getRaceId())
                .orElseThrow(() -> new IllegalArgumentException("Race not found."));

        if (race.getStatus() != RaceStatus.PUBLISHED && race.getStatus() != RaceStatus.PREPARE) {
            throw new IllegalArgumentException("Predictions are only allowed before the race starts.");
        }

        java.util.List<com.swp.hrtms.hrtmsbe.entity.RegistrationForm> lineup = registrationFormRepository
                .findByRace_Id(race.getId());
        long activeCount = lineup.stream()
                .filter(f -> f.getStatus() == com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING)
                .count();
        if (activeCount <= 1) {
            throw new IllegalArgumentException("Cannot place a prediction on a race with fewer than 2 active horses.");
        }

        if (!isHorseEligibleForPrediction(request.getRaceId(), request.getPredictedHorseId())) {
            throw new IllegalArgumentException("This horse is not eligible for prediction in this race.");
        }

        // BR_09: Predicted Time (Only enabled 1 hour beforehand and locks when race
        // begins)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime raceStartDateTime = LocalDateTime.of(race.getDate(), race.getStartTime());
        
        Integer predictionOpenHoursBefore = 1;
        if (race.getRaceRules() != null && race.getRaceRules().getPredictionTimeBefore() != null) {
            predictionOpenHoursBefore = race.getRaceRules().getPredictionTimeBefore();
        }

        // Bypassed for demo purposes
        if (now.isBefore(raceStartDateTime.minusHours(predictionOpenHoursBefore)) ||
                !now.isBefore(raceStartDateTime)) {
            throw new IllegalArgumentException("Predictions are only allowed within " + predictionOpenHoursBefore + " hour(s) before the race starts.");
        }

        // BR_10: Ticket Limit (1 prediction per spectator and race)
        if (predictionRepository.existsBySpectator_IdAndRace_Id(request.getSpectatorId(), request.getRaceId())) {
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
                .status(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING)
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : now)
                .build();

        prediction = predictionRepository.save(prediction);
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

    @Override
    public PredictionResponse getById(Integer id) {
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prediction not found."));
        // khai
        if (isDeleted(prediction)) {
            throw new IllegalArgumentException("Prediction not found.");
        }
        return toResponse(prediction);
    }

    @Override
    @Transactional
    public PredictionResponse update(Integer id, PredictionRequest request) {
        // khai
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prediction not found."));
        if (isDeleted(prediction)) {
            throw new IllegalArgumentException("Prediction not found.");
        }
        throw new IllegalArgumentException("Predictions cannot be updated after submission.");
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // khai
        Prediction prediction = predictionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prediction not found."));
        if (isDeleted(prediction)) {
            throw new IllegalArgumentException("Prediction not found.");
        }
        throw new IllegalArgumentException("Predictions cannot be deleted after submission.");
    }

    // khai
    private boolean isDeleted(Prediction prediction) {
        // khai
        return "CANCELLED".equalsIgnoreCase(prediction.getStatus() == null ? "" : prediction.getStatus().name());
    }

    private boolean isHorseEligibleForPrediction(Integer raceId, Integer horseId) {
        if (raceId == null || horseId == null) {
            return false;
        }
        return registrationFormRepository.findByRace_Id(raceId).stream()
                .anyMatch(form -> form.getHorse() != null
                        && horseId.equals(form.getHorse().getId())
                        && form.getStatus() == com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
    }

    private PredictionResponse toResponse(Prediction prediction) {
        return PredictionResponse.builder()
                .id(prediction.getId())
                .spectatorId(prediction.getSpectator() != null ? prediction.getSpectator().getId() : null)
                .raceId(prediction.getRace() != null ? prediction.getRace().getId() : null)
                .predictedHorseId(
                        prediction.getPredictedHorse() != null ? prediction.getPredictedHorse().getId() : null)
                .pointsInvested(prediction.getPointsInvested())
                .status(prediction.getStatus())
                .createdAt(prediction.getCreatedAt())
                .build();
    }
}
