package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.entity.Prediction;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.repository.PredictionRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import com.swp.hrtms.hrtmsbe.repository.TransactionRepository;
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

    public PredictionServiceImpl(PredictionRepository predictionRepository,
            RaceRepository raceRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {
        this.predictionRepository = predictionRepository;
        this.raceRepository = raceRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public PredictionResponse create(PredictionRequest request) {
        Race race = raceRepository.findById(request.getRaceId())
                .orElseThrow(() -> new IllegalArgumentException("Race not found."));

        // BR_09: Predicted Time (Only enabled 1 hour beforehand and locks when race
        // begins)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime raceStartDateTime = LocalDateTime.of(race.getDate(), race.getStartTime());
        if (now.isBefore(raceStartDateTime.minusHours(1)) || now.isAfter(raceStartDateTime)) {
            throw new IllegalArgumentException("Predictions are only allowed within 1 hour before the race starts.");
        }

        // BR_10: Ticket Limit (1 prediction per spectator and race)
        if (predictionRepository.existsBySpectatorIdAndRaceId(request.getSpectatorId(), request.getRaceId())) {
            throw new IllegalArgumentException("You have already made a prediction for this race.");
        }

        // BR_16: Deduct points
        Wallet wallet = walletRepository.findByUserId(request.getSpectatorId())
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
                .walletId(wallet.getId())
                .tournamentId(race.getTournament().getId())
                .raceId(race.getId())
                .horseId(request.getPredictedHorseId())
                .amount(-request.getPointsInvested())
                .type("PREDICTION_DEDUCT")
                .createdAt(now)
                .build();
        transactionRepository.save(tx);

        Prediction prediction = Prediction.builder()
                .spectatorId(request.getSpectatorId())
                .raceId(request.getRaceId())
                .predictedHorseId(request.getPredictedHorseId())
                .pointsInvested(request.getPointsInvested())
                // khai
                .status(request.getStatus() != null ? request.getStatus()
                        : com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING)
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

    private PredictionResponse toResponse(Prediction prediction) {
        return PredictionResponse.builder()
                .id(prediction.getId())
                .spectatorId(prediction.getSpectatorId())
                .raceId(prediction.getRaceId())
                .predictedHorseId(prediction.getPredictedHorseId())
                .pointsInvested(prediction.getPointsInvested())
                .status(prediction.getStatus())
                .createdAt(prediction.getCreatedAt())
                .build();
    }

    private boolean isDeleted(Prediction prediction) {
        return "CANCELLED".equalsIgnoreCase(prediction.getStatus() == null ? "" : prediction.getStatus().name());
    }
}
