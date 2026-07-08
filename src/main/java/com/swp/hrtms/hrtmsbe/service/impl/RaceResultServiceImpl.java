package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.RaceResultRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RaceResultResponse;
import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.repository.*;
import com.swp.hrtms.hrtmsbe.service.RaceResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    public RaceResultServiceImpl(RaceResultRepository raceResultRepository,
                                 RacePlacementRepository racePlacementRepository,
                                 PredictionRepository predictionRepository,
                                 WalletRepository walletRepository,
                                 TransactionRepository transactionRepository,
                                 RaceRepository raceRepository,
                                 RegistrationFormRepository registrationFormRepository,
                                 TournamentRepository tournamentRepository,
                                 RefereeRepository refereeRepository,
                                 HorseRepository horseRepository) {
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
    }

    @Override
    @Transactional
    public RaceResultResponse create(RaceResultRequest request) {
        RaceResult result = RaceResult.builder()
                .race(request.getRaceId() != null ? raceRepository.getReferenceById(request.getRaceId()) : null)
                .referee(request.getRefereeId() != null ? refereeRepository.getReferenceById(request.getRefereeId()) : null)
                .status(request.getStatus())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now())
                .build();
        
        result = raceResultRepository.save(result);

        //khai
        if (request.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL) {
            processRewards(result.getId(), request.getRaceId());
        }

        return toResponse(result);
    }

    @Override
    @Transactional
    public RaceResultResponse update(Integer id, RaceResultRequest request) {
        RaceResult result = raceResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RaceResult not found"));
        
        //khai
        boolean wasFinished = result.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL;
        result.setRace(request.getRaceId() != null ? raceRepository.getReferenceById(request.getRaceId()) : null);
        result.setReferee(request.getRefereeId() != null ? refereeRepository.getReferenceById(request.getRefereeId()) : null);
        result.setStatus(request.getStatus());
        if (request.getCreatedAt() != null) {
            result.setCreatedAt(request.getCreatedAt());
        }
        result = raceResultRepository.save(result);

        // If changed to OFFICIAL, process rewards
        if (!wasFinished && request.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL) {
            processRewards(result.getId(), request.getRaceId());
        }

        return toResponse(result);
    }

    private void processRewards(Integer raceResultId, Integer raceId) {
        // BR_11: Automatic Rewards (1-to-2 ratio)
        // Find the winning horse
        List<RacePlacement> placements = racePlacementRepository.findByRaceResult_Id(raceResultId);
        Optional<RacePlacement> winnerPlacement = placements.stream()
                .filter(p -> p.getFinishPosition() != null && p.getFinishPosition() == 1)
                .findFirst();

        if (winnerPlacement.isPresent()) {
            Integer winningFormId = winnerPlacement.get().getRegistrationForm() != null ? winnerPlacement.get().getRegistrationForm().getId() : null;
            RegistrationForm winningForm = winningFormId != null ? registrationFormRepository.findById(winningFormId).orElse(null) : null;

            if (winningForm != null) {
                Integer winningHorseId = winningForm.getHorse() != null ? winningForm.getHorse().getId() : null;

                // Find all predictions for this race
                List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);
                for (Prediction p : predictions) {
                    if (p.getPredictedHorse() != null && p.getPredictedHorse().getId().equals(winningHorseId) && p.getStatus() == com.swp.hrtms.hrtmsbe.enums.PredictionStatus.LOCKED) {
                        // Winner! Reward = invested * 2
                        Integer reward = p.getPointsInvested() * 2;
                        //khai
                        p.setStatus(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.DONE);
                        predictionRepository.save(p);

                        // Add to wallet
                        Integer spectatorUserId = p.getSpectator() != null ? p.getSpectator().getId() : null;
                        Wallet wallet = spectatorUserId != null ? walletRepository.findByUser_Id(spectatorUserId).orElse(null) : null;
                        if (wallet != null) {
                            wallet.setBalance(wallet.getBalance() + reward);
                            wallet.setUpdatedAt(LocalDateTime.now());
                            walletRepository.save(wallet);

                            // Save transaction
                            Transaction tx = Transaction.builder()
                                    .wallet(wallet)
                                    .race(raceRepository.getReferenceById(raceId))
                                    .horse(horseRepository.getReferenceById(winningHorseId))
                                    .amount(reward)
                                    .type("PREDICTION_REWARD")
                                    .createdAt(LocalDateTime.now())
                                    .build();
                            transactionRepository.save(tx);
                        }
                    } else if (p.getStatus() == com.swp.hrtms.hrtmsbe.enums.PredictionStatus.LOCKED) {
                        p.setStatus(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.DONE);
                        predictionRepository.save(p);
                    }
                }
            }
        }
        
        // Cập nhật trạng thái các đội đua (RegistrationForm) thành COMPLETE
        List<RegistrationForm> forms = registrationFormRepository.findByRace_Id(raceId);
        for (RegistrationForm form : forms) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.COMPLETE);
        }
        registrationFormRepository.saveAll(forms);

        // Also update Race status
        Race race = raceRepository.findById(raceId).orElse(null);
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

    @Override
    public List<RaceResultResponse> getAll() {
        return raceResultRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RaceResultResponse getById(Integer id) {
        RaceResult result = raceResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RaceResult not found"));
        return toResponse(result);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        RaceResult result = raceResultRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RaceResult not found"));
        //khai
        raceResultRepository.delete(result);
    }

    private RaceResultResponse toResponse(RaceResult result) {
        return RaceResultResponse.builder()
                .id(result.getId())
                .raceId(result.getRace() != null ? result.getRace().getId() : null)
                .refereeId(result.getReferee() != null ? result.getReferee().getId() : null)
                .status(result.getStatus())
                .createdAt(result.getCreatedAt())
                .build();
    }
}








