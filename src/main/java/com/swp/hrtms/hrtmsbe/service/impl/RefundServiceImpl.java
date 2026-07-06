package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.enums.NotificationStatus;
import com.swp.hrtms.hrtmsbe.enums.NotificationType;
import com.swp.hrtms.hrtmsbe.enums.PredictionStatus;
import com.swp.hrtms.hrtmsbe.repository.*;
import com.swp.hrtms.hrtmsbe.service.RefundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RefundServiceImpl implements RefundService {

    private final PredictionRepository predictionRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final UserRepository userRepository;

    public RefundServiceImpl(
            PredictionRepository predictionRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            NotificationRepository notificationRepository,
            NotificationRecipientRepository notificationRecipientRepository,
            UserRepository userRepository) {
        this.predictionRepository = predictionRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void refundPredictions(List<Prediction> predictions, String reason) {
        LocalDateTime now = LocalDateTime.now();

        // Admin could be the sender of the notification, find an admin or just use null
        User systemAdmin = userRepository.findByRoleIn(List.of("ADMIN")).stream().findFirst().orElse(null);

        for (Prediction prediction : predictions) {
            // Only refund if not already cancelled/refunded
            if (prediction.getStatus() != PredictionStatus.CANCELLED) {
                Spectator spectator = prediction.getSpectator();
                if (spectator == null)
                    continue;

                Wallet wallet = walletRepository.findByUserId(spectator.getId()).orElse(null);
                if (wallet != null) {
                    // Refund points
                    wallet.setBalance(wallet.getBalance() + prediction.getPointsInvested());
                    wallet.setUpdatedAt(now);
                    walletRepository.save(wallet);

                    // Record transaction
                    Transaction tx = Transaction.builder()
                            .wallet(wallet)
                            .tournament(prediction.getRace() != null ? prediction.getRace().getTournament() : null)
                            .race(prediction.getRace())
                            .horse(prediction.getPredictedHorse())
                            .amount(prediction.getPointsInvested()) // Positive amount for refund
                            .type("PREDICTION_REFUND")
                            .createdAt(now)
                            .build();
                    transactionRepository.save(tx);
                }

                // Update prediction status
                prediction.setStatus(PredictionStatus.CANCELLED);
                predictionRepository.save(prediction);

                // Notify the spectator
                Notification notification = Notification.builder()
                        .sender(systemAdmin)
                        .title("Points have been refunded.")
                        .content("You have been refunded " + prediction.getPointsInvested()
                                + " points for the prediction in race " +
                                (prediction.getRace() != null ? prediction.getRace().getName() : "") +
                                ". Reason: " + reason)
                        .type(NotificationType.REFUND)
                        .race(prediction.getRace())
                        .createdAt(now)
                        .build();
                notification = notificationRepository.save(notification);

                NotificationRecipient recipient = NotificationRecipient.builder()
                        .notification(notification)
                        .recipient(spectator)
                        .status(NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(recipient);
            }
        }
    }

    @Override
    @Transactional
    public void refundForRace(Integer raceId, String reason) {
        List<Prediction> predictions = predictionRepository.findByRace_Id(raceId);
        refundPredictions(predictions, reason);
    }

    @Override
    @Transactional
    public void refundForTournament(Integer tournamentId, String reason) {
        List<Prediction> predictions = predictionRepository.findByRace_Tournament_Id(tournamentId);
        refundPredictions(predictions, reason);
    }
}
