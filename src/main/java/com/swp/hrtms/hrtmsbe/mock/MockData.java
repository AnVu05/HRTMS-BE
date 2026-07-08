package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Doctor;
import com.swp.hrtms.hrtmsbe.entity.HealthCheck;
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
import com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus;
import com.swp.hrtms.hrtmsbe.enums.PredictionStatus;
import com.swp.hrtms.hrtmsbe.enums.RaceStatus;
import com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus;
import com.swp.hrtms.hrtmsbe.enums.TournamentStatus;
import com.swp.hrtms.hrtmsbe.enums.UserStatus;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.DoctorRepository;
import com.swp.hrtms.hrtmsbe.repository.HealthCheckRepository;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class MockData {

    @Bean
    public CommandLineRunner initRaceLifecycleDemoData(
            AdminRepository adminRepository,
            UserRepository userRepository,
            HorseOwnerRepository horseOwnerRepository,
            DoctorRepository doctorRepository,
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

    @Bean
    public CommandLineRunner initHealthCheckFlowDemoData(
            AdminRepository adminRepository,
            UserRepository userRepository,
            DoctorRepository doctorRepository,
            HorseOwnerRepository horseOwnerRepository,
            HorseRepository horseRepository,
            JockeyRepository jockeyRepository,
            RefereeRepository refereeRepository,
            SpectatorRepository spectatorRepository,
            TournamentRepository tournamentRepository,
            RaceRepository raceRepository,
            RegistrationFormRepository registrationFormRepository,
            HealthCheckRepository healthCheckRepository,
            PredictionRepository predictionRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository) {
        return args -> {
            if (userRepository.existsByUsername("demo_health_admin")) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            Admin admin = new Admin();
            admin.setUsername("demo_health_admin");
            admin.setPassword("123456");
            admin.setEmail("demo_health_admin@hrtms.local");
            admin.setRole("ADMIN");
            admin.setStatus(UserStatus.ACTIVE);
            admin.setAvatar("demo-health-admin-avatar");
            admin = adminRepository.save(admin);

            User doctorUser = createBaseUser("demo_health_doctor", "demo_health_doctor@hrtms.local", "DOCTOR");
            doctorUser = userRepository.save(doctorUser);
            Doctor doctor = doctorRepository.save(Doctor.builder()
                    .user(doctorUser)
                    .build());

            Referee referee = new Referee();
            referee.setUsername("demo_health_referee");
            referee.setPassword("123456");
            referee.setEmail("demo_health_referee@hrtms.local");
            referee.setRole("REFEREE");
            referee.setStatus(UserStatus.ACTIVE);
            referee.setName("Demo Health Referee");
            referee = refereeRepository.save(referee);

            Jockey acceptJockeyOne = jockeyRepository.save(createJockey("demo_health_accept_jockey_1",
                    "Demo Health Accept Jockey One", "demo_health_accept_jockey_1@hrtms.local"));
            Jockey acceptJockeyTwo = jockeyRepository.save(createJockey("demo_health_accept_jockey_2",
                    "Demo Health Accept Jockey Two", "demo_health_accept_jockey_2@hrtms.local"));
            Jockey rejectJockeyOne = jockeyRepository.save(createJockey("demo_health_reject_jockey_1",
                    "Demo Health Reject Jockey One", "demo_health_reject_jockey_1@hrtms.local"));
            Jockey rejectJockeyTwo = jockeyRepository.save(createJockey("demo_health_reject_jockey_2",
                    "Demo Health Reject Jockey Two", "demo_health_reject_jockey_2@hrtms.local"));

            User acceptOwnerUserOne = userRepository.save(createBaseUser("demo_health_accept_owner_1",
                    "demo_health_accept_owner_1@hrtms.local", "HORSE_OWNER"));
            HorseOwner acceptOwnerOne = horseOwnerRepository.save(HorseOwner.builder()
                    .user(acceptOwnerUserOne)
                    .ownerName("Demo Health Accept Owner One")
                    .avatar("demo-health-owner-avatar")
                    .build());

            User acceptOwnerUserTwo = userRepository.save(createBaseUser("demo_health_accept_owner_2",
                    "demo_health_accept_owner_2@hrtms.local", "HORSE_OWNER"));
            HorseOwner acceptOwnerTwo = horseOwnerRepository.save(HorseOwner.builder()
                    .user(acceptOwnerUserTwo)
                    .ownerName("Demo Health Accept Owner Two")
                    .avatar("demo-health-owner-avatar")
                    .build());

            User rejectOwnerUserOne = userRepository.save(createBaseUser("demo_health_reject_owner_1",
                    "demo_health_reject_owner_1@hrtms.local", "HORSE_OWNER"));
            HorseOwner rejectOwnerOne = horseOwnerRepository.save(HorseOwner.builder()
                    .user(rejectOwnerUserOne)
                    .ownerName("Demo Health Reject Owner One")
                    .avatar("demo-health-owner-avatar")
                    .build());

            User rejectOwnerUserTwo = userRepository.save(createBaseUser("demo_health_reject_owner_2",
                    "demo_health_reject_owner_2@hrtms.local", "HORSE_OWNER"));
            HorseOwner rejectOwnerTwo = horseOwnerRepository.save(HorseOwner.builder()
                    .user(rejectOwnerUserTwo)
                    .ownerName("Demo Health Reject Owner Two")
                    .avatar("demo-health-owner-avatar")
                    .build());

            Horse acceptHorseOne = horseRepository.save(createHorse(acceptOwnerOne, "Demo Health Pass Target"));
            Horse acceptHorseTwo = horseRepository.save(createHorse(acceptOwnerTwo, "Demo Health Pass Stablemate"));
            Horse rejectHorseOne = horseRepository.save(createHorse(rejectOwnerOne, "Demo Health Reject Target"));
            Horse rejectHorseTwo = horseRepository.save(createHorse(rejectOwnerTwo, "Demo Health Reject Survivor"));

            Spectator acceptRaceSpectatorOne = spectatorRepository.save(createSpectator("demo_health_accept_spectator_1",
                    "Demo Health Accept Spectator One", "demo_health_accept_spectator_1@hrtms.local"));
            Spectator acceptRaceSpectatorTwo = spectatorRepository.save(createSpectator("demo_health_accept_spectator_2",
                    "Demo Health Accept Spectator Two", "demo_health_accept_spectator_2@hrtms.local"));

            Wallet acceptRaceSpectatorOneWallet = walletRepository.save(Wallet.builder()
                    .user(acceptRaceSpectatorOne)
                    .balance(800)
                    .updatedAt(now)
                    .build());
            Wallet acceptRaceSpectatorTwoWallet = walletRepository.save(Wallet.builder()
                    .user(acceptRaceSpectatorTwo)
                    .balance(750)
                    .updatedAt(now)
                    .build());

            Tournament tournament = tournamentRepository.save(Tournament.builder()
                    .admin(admin)
                    .name("Demo Doctor Health Check Tournament")
                    .createdAt(now)
                    .publishedDate(LocalDate.now().minusDays(1))
                    .openPredictionDate(LocalDate.now().minusDays(1))
                    .closePredictionDate(LocalDate.now().plusDays(1))
                    .startDate(LocalDate.now().plusDays(3))
                    .endDate(LocalDate.now().plusDays(3))
                    .status(TournamentStatus.PUBLISHED)
                    .build());

            Race acceptRace = raceRepository.save(Race.builder()
                    .tournament(tournament)
                    .name("Demo Health Check ACCEPT Flow")
                    .date(LocalDate.now().plusDays(3))
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(9, 20))
                    .numHorse(2)
                    .distanceM(1000)
                    .referee(referee)
                    .status(RaceStatus.PUBLISHED)
                    .expectedDurationMinutes(10)
                    .breakTimeMinutes(5)
                    .build());

            Race rejectRace = raceRepository.save(Race.builder()
                    .tournament(tournament)
                    .name("Demo Health Check REJECT Walk Over Flow")
                    .date(LocalDate.now().plusDays(3))
                    .startTime(LocalTime.of(10, 0))
                    .endTime(LocalTime.of(10, 20))
                    .numHorse(2)
                    .distanceM(1000)
                    .referee(referee)
                    .status(RaceStatus.PUBLISHED)
                    .expectedDurationMinutes(10)
                    .breakTimeMinutes(5)
                    .build());

            RegistrationForm acceptTargetForm = registrationFormRepository.save(RegistrationForm.builder()
                    .owner(acceptOwnerOne)
                    .horse(acceptHorseOne)
                    .jockey(acceptJockeyOne)
                    .tournament(tournament)
                    .race(acceptRace)
                    .admin(admin)
                    .status(RegistrationFormStatus.HEALTH_CHECKING)
                    .createdAt(now.minusDays(1))
                    .build());
            RegistrationForm acceptStableForm = registrationFormRepository.save(RegistrationForm.builder()
                    .owner(acceptOwnerTwo)
                    .horse(acceptHorseTwo)
                    .jockey(acceptJockeyTwo)
                    .tournament(tournament)
                    .race(acceptRace)
                    .admin(admin)
                    .status(RegistrationFormStatus.RACING)
                    .createdAt(now.minusDays(1))
                    .build());

            RegistrationForm rejectTargetForm = registrationFormRepository.save(RegistrationForm.builder()
                    .owner(rejectOwnerOne)
                    .horse(rejectHorseOne)
                    .jockey(rejectJockeyOne)
                    .tournament(tournament)
                    .race(rejectRace)
                    .admin(admin)
                    .status(RegistrationFormStatus.HEALTH_CHECKING)
                    .createdAt(now.minusDays(1))
                    .build());
            RegistrationForm rejectSurvivorForm = registrationFormRepository.save(RegistrationForm.builder()
                    .owner(rejectOwnerTwo)
                    .horse(rejectHorseTwo)
                    .jockey(rejectJockeyTwo)
                    .tournament(tournament)
                    .race(rejectRace)
                    .admin(admin)
                    .status(RegistrationFormStatus.RACING)
                    .createdAt(now.minusDays(1))
                    .build());

            HealthCheck acceptHealthCheck = healthCheckRepository.save(HealthCheck.builder()
                    .registrationForm(acceptTargetForm)
                    .doctor(doctor)
                    .status(HealthCheckStatus.PENDING_DOCTOR)
                    .medicalNotes("Demo data: update this health check to ACCEPT.")
                    .checkDate(now.minusHours(1))
                    .build());

            HealthCheck acceptStableHealthCheck = healthCheckRepository.save(HealthCheck.builder()
                    .registrationForm(acceptStableForm)
                    .doctor(doctor)
                    .status(HealthCheckStatus.ACCEPT)
                    .medicalNotes("Demo data: this horse already passed health check.")
                    .checkDate(now.minusHours(2))
                    .build());

            HealthCheck rejectHealthCheck = healthCheckRepository.save(HealthCheck.builder()
                    .registrationForm(rejectTargetForm)
                    .doctor(doctor)
                    .status(HealthCheckStatus.CHECKING)
                    .medicalNotes("Demo data: update this health check to REJECT.")
                    .checkDate(now.minusHours(1))
                    .build());

            HealthCheck rejectSurvivorHealthCheck = healthCheckRepository.save(HealthCheck.builder()
                    .registrationForm(rejectSurvivorForm)
                    .doctor(doctor)
                    .status(HealthCheckStatus.ACCEPT)
                    .medicalNotes("Demo data: this horse already passed health check.")
                    .checkDate(now.minusHours(2))
                    .build());

            Prediction acceptRacePredictionOne = predictionRepository.save(Prediction.builder()
                    .spectator(acceptRaceSpectatorOne)
                    .race(acceptRace)
                    .predictedHorse(acceptHorseOne)
                    .pointsInvested(100)
                    .status(PredictionStatus.LOCKED)
                    .createdAt(now.minusMinutes(45))
                    .build());
            Prediction acceptRacePredictionTwo = predictionRepository.save(Prediction.builder()
                    .spectator(acceptRaceSpectatorTwo)
                    .race(acceptRace)
                    .predictedHorse(acceptHorseTwo)
                    .pointsInvested(120)
                    .status(PredictionStatus.LOCKED)
                    .createdAt(now.minusMinutes(40))
                    .build());

            transactionRepository.save(Transaction.builder()
                    .wallet(acceptRaceSpectatorOneWallet)
                    .tournament(tournament)
                    .race(acceptRace)
                    .horse(acceptHorseOne)
                    .amount(-100)
                    .type("PREDICTION_DEDUCT")
                    .createdAt(now.minusMinutes(45))
                    .build());
            transactionRepository.save(Transaction.builder()
                    .wallet(acceptRaceSpectatorTwoWallet)
                    .tournament(tournament)
                    .race(acceptRace)
                    .horse(acceptHorseTwo)
                    .amount(-120)
                    .type("PREDICTION_DEDUCT")
                    .createdAt(now.minusMinutes(40))
                    .build());

            System.out.println("=== HRTMS doctor health check flow demo data ===");
            System.out.println("Admin: demo_health_admin / 123456, adminId=" + admin.getId());
            System.out.println("Doctor: demo_health_doctor / 123456, doctorId=" + doctor.getUserId());
            System.out.println("ACCEPT test: raceId=" + acceptRace.getId()
                    + ", healthCheckId=" + acceptHealthCheck.getId()
                    + ", registrationFormId=" + acceptTargetForm.getId()
                    + ", stableRegistrationFormId=" + acceptStableForm.getId()
                    + ", stableHealthCheckId=" + acceptStableHealthCheck.getId()
                    + ", predictionIds=" + acceptRacePredictionOne.getId() + "," + acceptRacePredictionTwo.getId());
            System.out.println("Call: PUT /api/healthchecks/" + acceptHealthCheck.getId()
                    + " {\"doctorId\":" + doctor.getUserId()
                    + ",\"status\":\"ACCEPT\",\"medicalNotes\":\"Passed health check\"}");
            System.out.println("Expected: healthCheck=ACCEPT, registrationForm=RACING, owner gets READY_RACING.");
            System.out.println("REJECT/WALK_OVER test: raceId=" + rejectRace.getId()
                    + ", healthCheckId=" + rejectHealthCheck.getId()
                    + ", registrationFormId=" + rejectTargetForm.getId()
                    + ", survivorRegistrationFormId=" + rejectSurvivorForm.getId()
                    + ", survivorHealthCheckId=" + rejectSurvivorHealthCheck.getId());
            System.out.println("Call: PUT /api/healthchecks/" + rejectHealthCheck.getId()
                    + " {\"doctorId\":" + doctor.getUserId()
                    + ",\"status\":\"REJECT\",\"medicalNotes\":\"Failed health check\"}");
            System.out.println("Expected: healthCheck=REJECT, registrationForm=DISQUALIFIED, race=WALK_OVER, survivor wins with OFFICIAL result and placement=1, prediction is blocked.");
            System.out.println("================================================");
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

    private static Horse createHorse(HorseOwner owner, String name) {
        return Horse.builder()
                .owner(owner)
                .name(name)
                .age(4)
                .breed("Thoroughbred")
                .sex("MALE")
                .weightKg(new BigDecimal("505.00"))
                .status(HorseStatus.WORK)
                .build();
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
