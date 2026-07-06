package com.swp.hrtms.hrtmsbe.mock;

// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Horse;
import com.swp.hrtms.hrtmsbe.entity.HorseOwner;
import com.swp.hrtms.hrtmsbe.entity.HorseStatus;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.entity.Prediction;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.entity.Spectator;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.entity.Transaction;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.entity.Wallet;
import com.swp.hrtms.hrtmsbe.enums.PredictionStatus;
import com.swp.hrtms.hrtmsbe.enums.RaceStatus;
import com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus;
import com.swp.hrtms.hrtmsbe.enums.TournamentStatus;
import com.swp.hrtms.hrtmsbe.enums.UserStatus;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
import com.swp.hrtms.hrtmsbe.repository.JockeyRepository;
import com.swp.hrtms.hrtmsbe.repository.PredictionRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.repository.SpectatorRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.repository.TransactionRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class MockData {

    @Bean
    public CommandLineRunner initRaceLifecycleDemoData(
            AdminRepository adminRepository,
            UserRepository userRepository,
            HorseOwnerRepository horseOwnerRepository,
            HorseRepository horseRepository,
            JockeyRepository jockeyRepository,
            RefereeRepository refereeRepository,
            SpectatorRepository spectatorRepository,
            TournamentRepository tournamentRepository,
            RaceRepository raceRepository,
            RegistrationFormRepository registrationFormRepository,
            PredictionRepository predictionRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {
        return args -> {
            if (userRepository.existsByUsername("demo_admin")) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            Admin admin = new Admin();
            admin.setUsername("demo_admin");
            admin.setPassword("123456");
            admin.setEmail("demo_admin@hrtms.local");
            admin.setRole("ADMIN");
            admin.setStatus(UserStatus.ACTIVE);
            admin.setAvatar("demo-admin-avatar");
            admin = adminRepository.save(admin);

            Referee referee = new Referee();
            referee.setUsername("demo_referee");
            referee.setPassword("123456");
            referee.setEmail("demo_referee@hrtms.local");
            referee.setRole("REFEREE");
            referee.setStatus(UserStatus.ACTIVE);
            referee.setName("Demo Referee");
            referee = refereeRepository.save(referee);

            Jockey jockeyOne = createJockey("demo_jockey_1", "Demo Jockey One", "demo_jockey_1@hrtms.local");
            jockeyOne = jockeyRepository.save(jockeyOne);

            Jockey jockeyTwo = createJockey("demo_jockey_2", "Demo Jockey Two", "demo_jockey_2@hrtms.local");
            jockeyTwo = jockeyRepository.save(jockeyTwo);

            User ownerUserOne = createBaseUser("demo_owner_1", "demo_owner_1@hrtms.local", "HORSE_OWNER");
            ownerUserOne = userRepository.save(ownerUserOne);
            HorseOwner ownerOne = horseOwnerRepository.save(HorseOwner.builder()
                    .user(ownerUserOne)
                    .ownerName("Demo Owner One")
                    .avatar("demo-owner-one-avatar")
                    .build());

            User ownerUserTwo = createBaseUser("demo_owner_2", "demo_owner_2@hrtms.local", "HORSE_OWNER");
            ownerUserTwo = userRepository.save(ownerUserTwo);
            HorseOwner ownerTwo = horseOwnerRepository.save(HorseOwner.builder()
                    .user(ownerUserTwo)
                    .ownerName("Demo Owner Two")
                    .avatar("demo-owner-two-avatar")
                    .build());

            Horse horseOne = horseRepository.save(Horse.builder()
                    .owner(ownerOne)
                    .name("Demo Thunder")
                    .age(4)
                    .breed("Thoroughbred")
                    .sex("MALE")
                    .weightKg(new BigDecimal("510.50"))
                    .status(HorseStatus.WORK)
                    .build());

            Horse horseTwo = horseRepository.save(Horse.builder()
                    .owner(ownerTwo)
                    .name("Demo Lightning")
                    .age(5)
                    .breed("Arabian")
                    .sex("FEMALE")
                    .weightKg(new BigDecimal("498.00"))
                    .status(HorseStatus.WORK)
                    .build());

            Spectator spectatorWinner = createSpectator("demo_spectator_win", "Winner Spectator",
                    "demo_spectator_win@hrtms.local");
            spectatorWinner = spectatorRepository.save(spectatorWinner);

            Spectator spectatorLose = createSpectator("demo_spectator_lose", "Lose Spectator",
                    "demo_spectator_lose@hrtms.local");
            spectatorLose = spectatorRepository.save(spectatorLose);

            Wallet winnerWallet = walletRepository.save(Wallet.builder()
                    .user(spectatorWinner)
                    .balance(900)
                    .updatedAt(now)
                    .build());

            Wallet loseWallet = walletRepository.save(Wallet.builder()
                    .user(spectatorLose)
                    .balance(850)
                    .updatedAt(now)
                    .build());

            Tournament tournament = tournamentRepository.save(Tournament.builder()
                    .admin(admin)
                    .name("Demo Referee Race Lifecycle Tournament")
                    .createdAt(now)
                    .publishedDate(LocalDate.now().minusDays(7))
                    .openPredictionDate(LocalDate.now().minusDays(7))
                    .closePredictionDate(LocalDate.now())
                    .startDate(LocalDate.now().minusDays(1))
                    .endDate(LocalDate.now().minusDays(1))
                    .status(TournamentStatus.PUBLISHED)
                    .build());

            Race race = raceRepository.save(Race.builder()
                    .tournament(tournament)
                    .name("Demo Race Lifecycle Flow")
                    .date(LocalDate.now())
                    .startTime(LocalTime.now().minusMinutes(5))
                    .endTime(LocalTime.now().plusMinutes(20))
                    .numHorse(2)
                    .distanceM(1200)
                    .referee(referee)
                    .status(RaceStatus.PREPARE)
                    .expectedDurationMinutes(10)
                    .breakTimeMinutes(5)
                    .build());

            RegistrationForm formOne = registrationFormRepository.save(RegistrationForm.builder()
                    .owner(ownerOne)
                    .horse(horseOne)
                    .jockey(jockeyOne)
                    .tournament(tournament)
                    .race(race)
                    .admin(admin)
                    .status(RegistrationFormStatus.PREPARE)
                    .createdAt(now.minusDays(2))
                    .build());

            RegistrationForm formTwo = registrationFormRepository.save(RegistrationForm.builder()
                    .owner(ownerTwo)
                    .horse(horseTwo)
                    .jockey(jockeyTwo)
                    .tournament(tournament)
                    .race(race)
                    .admin(admin)
                    .status(RegistrationFormStatus.PREPARE)
                    .createdAt(now.minusDays(2))
                    .build());

            predictionRepository.save(Prediction.builder()
                    .spectator(spectatorWinner)
                    .race(race)
                    .predictedHorse(horseOne)
                    .pointsInvested(100)
                    .status(PredictionStatus.PENDING)
                    .createdAt(now.minusMinutes(20))
                    .build());

            predictionRepository.save(Prediction.builder()
                    .spectator(spectatorLose)
                    .race(race)
                    .predictedHorse(horseTwo)
                    .pointsInvested(150)
                    .status(PredictionStatus.PENDING)
                    .createdAt(now.minusMinutes(18))
                    .build());

            transactionRepository.save(Transaction.builder()
                    .wallet(winnerWallet)
                    .tournament(tournament)
                    .race(race)
                    .horse(horseOne)
                    .amount(-100)
                    .type("PREDICTION_DEDUCT")
                    .createdAt(now.minusMinutes(20))
                    .build());

            transactionRepository.save(Transaction.builder()
                    .wallet(loseWallet)
                    .tournament(tournament)
                    .race(race)
                    .horse(horseTwo)
                    .amount(-150)
                    .type("PREDICTION_DEDUCT")
                    .createdAt(now.minusMinutes(18))
                    .build());

            System.out.println("=== HRTMS race lifecycle demo data ===");
            System.out.println("Admin: demo_admin / 123456");
            System.out.println("Referee: demo_referee / 123456, refereeId=" + referee.getId());
            System.out.println("Race id=" + race.getId() + " status=PREPARE");
            System.out.println("Winning registrationFormId=" + formOne.getId() + ", horseId=" + horseOne.getId());
            System.out.println("Second registrationFormId=" + formTwo.getId() + ", horseId=" + horseTwo.getId());
            System.out.println("Demo API flow:");
            System.out.println("1) PUT /api/v1/races/" + race.getId() + "/start");
            System.out.println("2) POST /api/raceresults {\"raceId\":" + race.getId()
                    + ",\"refereeId\":" + referee.getId() + ",\"status\":\"TEMPORARY\"}");
            System.out.println("3) POST /api/raceplacements with raceResultId from step 2 and registrationFormId="
                    + formOne.getId() + ", finishPosition=1");
            System.out.println("4) POST /api/raceplacements with raceResultId from step 2 and registrationFormId="
                    + formTwo.getId() + ", finishPosition=2");
            System.out.println("5) PUT /api/raceresults/{raceResultId} {\"raceId\":" + race.getId()
                    + ",\"refereeId\":" + referee.getId() + ",\"status\":\"OFFICIAL\"}");
            System.out.println("======================================");
        };
    }

    private static User createBaseUser(String username, String email, String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("123456");
        user.setEmail(email);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    private static Jockey createJockey(String username, String jockeyName, String email) {
        Jockey jockey = new Jockey();
        jockey.setUsername(username);
        jockey.setPassword("123456");
        jockey.setEmail(email);
        jockey.setRole("JOCKEY");
        jockey.setStatus(UserStatus.ACTIVE);
        jockey.setJockeyName(jockeyName);
        jockey.setExperienceYears(5);
        jockey.setAge(28);
        jockey.setProfessionalBio("Demo jockey for referee race lifecycle flow.");
        jockey.setAvatar("demo-jockey-avatar");
        return jockey;
    }

    private static Spectator createSpectator(String username, String displayName, String email) {
        Spectator spectator = new Spectator();
        spectator.setUsername(username);
        spectator.setPassword("123456");
        spectator.setEmail(email);
        spectator.setRole("SPECTATOR");
        spectator.setStatus(UserStatus.ACTIVE);
        spectator.setDisplayName(displayName);
        spectator.setAvatar("demo-spectator-avatar");
        return spectator;
    }
}
