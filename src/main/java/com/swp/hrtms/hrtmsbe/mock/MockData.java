package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.enums.NotificationStatus;
import com.swp.hrtms.hrtmsbe.enums.NotificationType;
import com.swp.hrtms.hrtmsbe.enums.RaceStatus;
import com.swp.hrtms.hrtmsbe.enums.TournamentStatus;
import com.swp.hrtms.hrtmsbe.enums.UserStatus;
import com.swp.hrtms.hrtmsbe.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class MockData {

    private static final String PREDICTION_SPECTATOR_USERNAME = "mock_prediction_spectator";

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SpectatorRepository spectatorRepository;
    private final HorseOwnerRepository horseOwnerRepository;
    private final HorseRepository horseRepository;
    private final WalletRepository walletRepository;
    private final TournamentRepository tournamentRepository;
    private final RaceRepository raceRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    @PostConstruct
    @Transactional
    public void seedPredictionNotificationMockData() {
        if (userRepository.existsByUsername(PREDICTION_SPECTATOR_USERNAME)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        Admin admin = new Admin();
        admin.setUsername("mock_prediction_admin");
        admin.setPassword("123456");
        admin.setEmail("mock_prediction_admin@example.com");
        admin.setRole("ADMIN");
        admin.setStatus(UserStatus.ACTIVE);
        admin = adminRepository.save(admin);

        Spectator spectator = new Spectator();
        spectator.setUsername(PREDICTION_SPECTATOR_USERNAME);
        spectator.setPassword("123456");
        spectator.setEmail("mock_prediction_spectator@example.com");
        spectator.setRole("SPECTATOR");
        spectator.setStatus(UserStatus.ACTIVE);
        spectator.setDisplayName("Prediction Test Spectator");
        spectator = spectatorRepository.save(spectator);

        Wallet wallet = walletRepository.save(Wallet.builder()
                .user(spectator)
                .balance(1040)
                .updatedAt(now)
                .build());

        User ownerUser = new User();
        ownerUser.setUsername("mock_prediction_owner");
        ownerUser.setPassword("123456");
        ownerUser.setEmail("mock_prediction_owner@example.com");
        ownerUser.setRole("HORSE_OWNER");
        ownerUser.setStatus(UserStatus.ACTIVE);
        ownerUser = userRepository.save(ownerUser);

        HorseOwner owner = horseOwnerRepository.save(HorseOwner.builder()
                .user(ownerUser)
                .ownerName("Prediction Test Owner")
                .build());

        Horse winningHorse = horseRepository.save(Horse.builder()
                .owner(owner)
                .name("Silver Arrow")
                .age(4)
                .breed("Thoroughbred")
                .sex("MALE")
                .weightKg(new BigDecimal("480.00"))
                .status(HorseStatus.WORK)
                .build());

        Horse losingHorse = horseRepository.save(Horse.builder()
                .owner(owner)
                .name("Blue Comet")
                .age(5)
                .breed("Thoroughbred")
                .sex("FEMALE")
                .weightKg(new BigDecimal("455.00"))
                .status(HorseStatus.WORK)
                .build());

        Tournament tournament = tournamentRepository.save(Tournament.builder()
                .admin(admin)
                .name("Prediction Notification Mock Cup")
                .createdAt(now.minusDays(2))
                .startDate(LocalDate.now().minusDays(1))
                .endDate(LocalDate.now().plusDays(1))
                .publishedDate(LocalDate.now().minusDays(2))
                .openPredictionDate(LocalDate.now().minusDays(2))
                .closePredictionDate(LocalDate.now())
                .status(TournamentStatus.PUBLISHED)
                .build());

        Race wonRace = raceRepository.save(Race.builder()
                .tournament(tournament)
                .name("Mock Race - Correct Prediction")
                .date(LocalDate.now())
                .startTime(LocalTime.now().minusHours(3))
                .endTime(LocalTime.now().minusHours(2))
                .numHorse(2)
                .distanceM(1200)
                .status(RaceStatus.COMPLETE)
                .build());

        Race lostRace = raceRepository.save(Race.builder()
                .tournament(tournament)
                .name("Mock Race - Wrong Prediction")
                .date(LocalDate.now())
                .startTime(LocalTime.now().minusHours(2))
                .endTime(LocalTime.now().minusHours(1))
                .numHorse(2)
                .distanceM(1400)
                .status(RaceStatus.COMPLETE)
                .build());

        transactionRepository.save(Transaction.builder()
                .wallet(wallet)
                .tournament(tournament)
                .race(wonRace)
                .horse(winningHorse)
                .amount(80)
                .type("PREDICTION_REWARD")
                .createdAt(now.minusMinutes(20))
                .build());

        transactionRepository.save(Transaction.builder()
                .wallet(wallet)
                .tournament(tournament)
                .race(lostRace)
                .horse(losingHorse)
                .amount(-40)
                .type("PREDICTION_DEDUCT")
                .createdAt(now.minusMinutes(15))
                .build());

        createPredictionNotification(admin, spectator, wonRace, now.minusMinutes(19));
        createPredictionNotification(admin, spectator, lostRace, now.minusMinutes(14));
    }

    private void createPredictionNotification(Admin admin, Spectator spectator, Race race, LocalDateTime createdAt) {
        Notification notification = notificationRepository.save(Notification.builder()
                .sender(admin)
                .race(race)
                .title("Prediction result")
                .content("Prediction result is ready.")
                .type(NotificationType.PREDICTED)
                .createdAt(createdAt)
                .build());

        notificationRecipientRepository.save(NotificationRecipient.builder()
                .notification(notification)
                .recipient(spectator)
                .status(NotificationStatus.UNREAD)
                .build());
    }
}
