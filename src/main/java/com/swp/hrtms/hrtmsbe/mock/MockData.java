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

        private final com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository;
        private final com.swp.hrtms.hrtmsbe.repository.AdminRepository adminRepository;
        private final com.swp.hrtms.hrtmsbe.repository.DoctorRepository doctorRepository;
        private final com.swp.hrtms.hrtmsbe.repository.JockeyRepository jockeyRepository;
        private final com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository horseOwnerRepository;
        private final com.swp.hrtms.hrtmsbe.repository.TournamentRepository tournamentRepository;
        private final com.swp.hrtms.hrtmsbe.repository.RaceRepository raceRepository;
        private final com.swp.hrtms.hrtmsbe.repository.RaceFormatRepository raceFormatRepository;
        private final com.swp.hrtms.hrtmsbe.repository.HorseRepository horseRepository;
        private final com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository registrationFormRepository;
        private final com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository jockeyCertRepository;
        private final com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository;
        private final com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository;
        private final com.swp.hrtms.hrtmsbe.repository.RefereeRepository refereeRepository;
        private final com.swp.hrtms.hrtmsbe.repository.RaceResultRepository raceResultRepository;
        private final com.swp.hrtms.hrtmsbe.repository.RacePlacementRepository racePlacementRepository;
        private final com.swp.hrtms.hrtmsbe.repository.HealthCheckRepository healthCheckRepository;
        private final com.swp.hrtms.hrtmsbe.repository.SpectatorRepository spectatorRepository;
        private final com.swp.hrtms.hrtmsbe.repository.WalletRepository walletRepository;
        private final com.swp.hrtms.hrtmsbe.repository.PredictionRepository predictionRepository;

        @jakarta.annotation.PostConstruct
        @Transactional
        public void init() {
                if (!userRepository.existsByUsername("admin")) {
                        // 1. Create Admin
                        com.swp.hrtms.hrtmsbe.entity.Admin admin = new com.swp.hrtms.hrtmsbe.entity.Admin();
                        admin.setUsername("admin");
                        admin.setPassword("123456");
                        admin.setEmail("vudin@gmail.com");
                        admin.setRole("ADMIN");
                        admin.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        adminRepository.save(admin);

                        // 2. Create Referee
                        com.swp.hrtms.hrtmsbe.entity.Referee referee1 = new com.swp.hrtms.hrtmsbe.entity.Referee();
                        referee1.setUsername("referee_paul");
                        referee1.setPassword("123456");
                        referee1.setEmail("paul@hrtms.com");
                        referee1.setRole("REFEREE");
                        referee1.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        refereeRepository.save(referee1);

                        // 2.5 Create Doctor
                        com.swp.hrtms.hrtmsbe.entity.Doctor doctor1 = new com.swp.hrtms.hrtmsbe.entity.Doctor();
                        com.swp.hrtms.hrtmsbe.entity.User doctorUser = new com.swp.hrtms.hrtmsbe.entity.User();
                        doctorUser.setUsername("doctor_jane");
                        doctorUser.setPassword("123456");
                        doctorUser.setEmail("jane@hrtms.com");
                        doctorUser.setRole("DOCTOR");
                        doctorUser.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        doctorUser = userRepository.save(doctorUser);
                        doctor1.setUser(doctorUser);
                        doctorRepository.save(doctor1);

                        // Create Jockey
                        com.swp.hrtms.hrtmsbe.entity.Jockey jockey1 = new com.swp.hrtms.hrtmsbe.entity.Jockey();
                        jockey1.setUsername("jockey_bold");
                        jockey1.setPassword("123456");
                        jockey1.setEmail("jockey_mock@hrtms.com");
                        jockey1.setRole("JOCKEY");
                        jockey1.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        jockey1.setJockeyName("Mock Jockey");
                        jockey1.setExperienceYears(3);
                        jockey1.setAge(25);
                        jockeyRepository.save(jockey1);

                        // Create Owner
                        com.swp.hrtms.hrtmsbe.entity.HorseOwner owner1 = new com.swp.hrtms.hrtmsbe.entity.HorseOwner();
                        com.swp.hrtms.hrtmsbe.entity.User ownerUser = new com.swp.hrtms.hrtmsbe.entity.User();
                        ownerUser.setUsername("owner_allie");
                        ownerUser.setPassword("123456");
                        ownerUser.setEmail("owner_mock@hrtms.com");
                        ownerUser.setRole("HORSE_OWNER");
                        ownerUser.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        ownerUser = userRepository.save(ownerUser);
                        owner1.setUser(ownerUser);
                        owner1.setOwnerName("Mock Owner");
                        horseOwnerRepository.save(owner1);

                        // Create Spectator
                        com.swp.hrtms.hrtmsbe.entity.Spectator spectator1 = new com.swp.hrtms.hrtmsbe.entity.Spectator();
                        spectator1.setUsername("spectator_mock");
                        spectator1.setPassword("123456");
                        spectator1.setEmail("spectator_mock@hrtms.com");
                        spectator1.setRole("SPECTATOR");
                        spectator1.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        spectator1.setDisplayName("Mock Spectator");
                        spectatorRepository.save(spectator1);

                        Wallet spectator1Wallet = Wallet.builder()
                                        .user(spectator1)
                                        .balance(1000)
                                        .updatedAt(LocalDateTime.now())
                                        .build();
                        walletRepository.save(spectator1Wallet);
                }

                if (!userRepository.existsByUsername("jockey_mock2")) {
                        // Create Jockey 2
                        com.swp.hrtms.hrtmsbe.entity.Jockey jockey2 = new com.swp.hrtms.hrtmsbe.entity.Jockey();
                        jockey2.setUsername("jockey_mock2");
                        jockey2.setPassword("123456");
                        jockey2.setEmail("jockey_mock2@hrtms.com");
                        jockey2.setRole("JOCKEY");
                        jockey2.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        jockey2.setJockeyName("Mock Jockey 2");
                        jockey2.setExperienceYears(4);
                        jockey2.setAge(26);
                        jockey2 = jockeyRepository.save(jockey2);

                        JockeyCert cert2 = JockeyCert.builder()
                                        .certName("Thoroughbred")
                                        .jockey(jockey2)
                                        .status(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.VERIFIED)
                                        .issuedAt(LocalDate.now())
                                        .build();
                        jockeyCertRepository.save(cert2);

                        // Create Jockey 3
                        com.swp.hrtms.hrtmsbe.entity.Jockey jockey3 = new com.swp.hrtms.hrtmsbe.entity.Jockey();
                        jockey3.setUsername("jockey_mock3");
                        jockey3.setPassword("123456");
                        jockey3.setEmail("jockey_mock3@hrtms.com");
                        jockey3.setRole("JOCKEY");
                        jockey3.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        jockey3.setJockeyName("Mock Jockey 3");
                        jockey3.setExperienceYears(5);
                        jockey3.setAge(27);
                        jockey3 = jockeyRepository.save(jockey3);

                        JockeyCert cert3 = JockeyCert.builder()
                                        .certName("Thoroughbred")
                                        .jockey(jockey3)
                                        .status(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.VERIFIED)
                                        .issuedAt(LocalDate.now())
                                        .build();
                        jockeyCertRepository.save(cert3);

                        // Create Owner 2
                        com.swp.hrtms.hrtmsbe.entity.HorseOwner owner2 = new com.swp.hrtms.hrtmsbe.entity.HorseOwner();
                        com.swp.hrtms.hrtmsbe.entity.User ownerUser2 = new com.swp.hrtms.hrtmsbe.entity.User();
                        ownerUser2.setUsername("owner_mock2");
                        ownerUser2.setPassword("123456");
                        ownerUser2.setEmail("owner_mock2@hrtms.com");
                        ownerUser2.setRole("HORSE_OWNER");
                        ownerUser2.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        ownerUser2 = userRepository.save(ownerUser2);
                        owner2.setUser(ownerUser2);
                        owner2.setOwnerName("Mock Owner 2");
                        owner2 = horseOwnerRepository.save(owner2);

                        Horse horse2 = Horse.builder()
                                        .name("Thoroughbred Horse 2")
                                        .breed("Thoroughbred")
                                        .owner(owner2)
                                        .age(5)
                                        .sex("Stallion")
                                        .weightKg(new java.math.BigDecimal("500.0"))
                                        .status(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK)
                                        .build();
                        horseRepository.save(horse2);

                        // Create Owner 3
                        com.swp.hrtms.hrtmsbe.entity.HorseOwner owner3 = new com.swp.hrtms.hrtmsbe.entity.HorseOwner();
                        com.swp.hrtms.hrtmsbe.entity.User ownerUser3 = new com.swp.hrtms.hrtmsbe.entity.User();
                        ownerUser3.setUsername("owner_mock3");
                        ownerUser3.setPassword("123456");
                        ownerUser3.setEmail("owner_mock3@hrtms.com");
                        ownerUser3.setRole("HORSE_OWNER");
                        ownerUser3.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        ownerUser3 = userRepository.save(ownerUser3);
                        owner3.setUser(ownerUser3);
                        owner3.setOwnerName("Mock Owner 3");
                        owner3 = horseOwnerRepository.save(owner3);

                        Horse horse3 = Horse.builder()
                                        .name("Thoroughbred Horse 3")
                                        .breed("Thoroughbred")
                                        .owner(owner3)
                                        .age(6)
                                        .sex("Mare")
                                        .weightKg(new java.math.BigDecimal("480.0"))
                                        .status(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK)
                                        .build();
                        horseRepository.save(horse3);
                }

                if (tournamentRepository.count() == 0) {
                        com.swp.hrtms.hrtmsbe.entity.User adminUser = userRepository.findByUsername("admin")
                                        .orElse(null);
                        com.swp.hrtms.hrtmsbe.entity.Admin admin = adminUser != null
                                        ? adminRepository.findById(adminUser.getId()).orElse(null)
                                        : null;

                        com.swp.hrtms.hrtmsbe.entity.User refUser = userRepository.findByUsername("referee_paul")
                                        .orElse(null);
                        com.swp.hrtms.hrtmsbe.entity.Referee referee = refUser != null
                                        ? refereeRepository.findById(refUser.getId()).orElse(null)
                                        : null;

                        if (admin != null && referee != null) {
                                // 1. Create RaceFormat with realistic English name and rules
                                RaceFormat format = RaceFormat.builder()
                                                .name("Dubai World Cup Sprint Format")
                                                .description("Official championship sprint rules for elite Thoroughbred horses")
                                                .entryFee(100.0)
                                                .firstPrizePercent(50.0)
                                                .secondPrizePercent(30.0)
                                                .thirdPrizePercent(20.0)
                                                .allowedBreed("Thoroughbred")
                                                .allowedHorseAge(3)
                                                .minJockeyExperience(1)
                                                .minWeight(300)
                                                .maxWeight(600)
                                                .baseWeight(50)
                                                .applyFemaleAllowance(0)
                                                .predictionTimeBefore(1) // Prediction opens 1 hour before start
                                                .healthCheckTimeBefore(48)
                                                .status(com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus.ACTIVE)
                                                .build();
                                format = raceFormatRepository.save(format);

                                // 2. Create Tournament with realistic English name
                                Tournament tournament = Tournament.builder()
                                                .name("Dubai World Cup Invitational 2026")
                                                .admin(admin)
                                                .createdAt(LocalDateTime.now())
                                                .startDate(LocalDate.now())
                                                .endDate(LocalDate.now().plusDays(7))
                                                .publishedDate(LocalDate.now().minusDays(1))
                                                .openPredictionDate(LocalDate.now().minusDays(1))
                                                .closePredictionDate(LocalDate.now().plusDays(7))
                                                .status(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED)
                                                .build();
                                tournament = tournamentRepository.save(tournament);

                                // 3. Create Race starting in 30 minutes (within the 1-hour prediction window)
                                Race race = Race.builder()
                                                .tournament(tournament)
                                                .name("Pegasus Sprint Championship - Heat 1")
                                                .date(LocalDate.now())
                                                .startTime(LocalTime.now().plusMinutes(30))
                                                .endTime(LocalTime.now().plusMinutes(60))
                                                .numHorse(10)
                                                .distanceM(1200)
                                                .referee(referee)
                                                .status(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED)
                                                .raceRules(format)
                                                .expectedDurationMinutes(30)
                                                .breakTimeMinutes(15)
                                                .build();
                                race = raceRepository.save(race);

                                // 4. Create 7 RegistrationForms with status RACING using realistic world-class entity data
                                String[] jockeyUsernames = {"jockey_frankie", "jockey_ryan", "jockey_christophe", "jockey_irad", "jockey_yutaka", "jockey_william", "jockey_joel"};
                                String[] jockeyNames = {"Frankie Dettori", "Ryan Moore", "Christophe Lemaire", "Irad Ortiz Jr.", "Yutaka Take", "William Buick", "Joel Rosario"};
                                int[] jockeyExp = {25, 20, 22, 12, 35, 18, 17};
                                int[] jockeyAges = {53, 40, 44, 31, 55, 35, 39};

                                String[] ownerUsernames = {"owner_mohammed", "owner_stewart", "owner_coolmore", "owner_godolphin", "owner_shadwell", "owner_juddmonte", "owner_winstar"};
                                String[] ownerNames = {"Sheikh Mohammed", "John Stewart", "Coolmore Stud Owner", "Godolphin Stable", "Shadwell Estate", "Juddmonte Racing", "WinStar Farm"};

                                String[] horseNames = {"Flightline", "Secretariat", "Justify", "American Pharoah", "City of Troy", "Enable", "Golden Sixty"};
                                int[] horseAges = {5, 4, 6, 5, 4, 6, 7};
                                String[] horseSexes = {"Stallion", "Stallion", "Stallion", "Stallion", "Colt", "Mare", "Gelding"};
                                double[] horseWeights = {520.0, 535.0, 510.0, 500.0, 490.0, 480.0, 495.0};

                                for (int i = 0; i < 7; i++) {
                                        final String uJockey = jockeyUsernames[i];
                                        com.swp.hrtms.hrtmsbe.entity.Jockey jockey = jockeyRepository.findAll().stream()
                                                        .filter(j -> uJockey.equals(j.getUsername()))
                                                        .findFirst()
                                                        .orElse(null);

                                        if (jockey == null) {
                                                jockey = new com.swp.hrtms.hrtmsbe.entity.Jockey();
                                                jockey.setUsername(uJockey);
                                                jockey.setPassword("123456");
                                                jockey.setEmail(uJockey + "@hrtms.com");
                                                jockey.setRole("JOCKEY");
                                                jockey.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                                                jockey.setJockeyName(jockeyNames[i]);
                                                jockey.setExperienceYears(jockeyExp[i]);
                                                jockey.setAge(jockeyAges[i]);
                                                jockey = jockeyRepository.save(jockey);

                                                JockeyCert cert = JockeyCert.builder()
                                                                .certName("Thoroughbred")
                                                                .jockey(jockey)
                                                                .status(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.VERIFIED)
                                                                .issuedAt(LocalDate.now())
                                                                .build();
                                                jockeyCertRepository.save(cert);
                                        }

                                        final String uOwner = ownerUsernames[i];
                                        com.swp.hrtms.hrtmsbe.entity.HorseOwner owner = horseOwnerRepository.findAll().stream()
                                                        .filter(o -> o.getUser() != null && uOwner.equals(o.getUser().getUsername()))
                                                        .findFirst()
                                                        .orElse(null);

                                        if (owner == null) {
                                                owner = new com.swp.hrtms.hrtmsbe.entity.HorseOwner();
                                                com.swp.hrtms.hrtmsbe.entity.User ownerUser = new com.swp.hrtms.hrtmsbe.entity.User();
                                                ownerUser.setUsername(uOwner);
                                                ownerUser.setPassword("123456");
                                                ownerUser.setEmail(uOwner + "@hrtms.com");
                                                ownerUser.setRole("HORSE_OWNER");
                                                ownerUser.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                                                ownerUser = userRepository.save(ownerUser);
                                                owner.setUser(ownerUser);
                                                owner.setOwnerName(ownerNames[i]);
                                                owner = horseOwnerRepository.save(owner);
                                        }

                                        final String hName = horseNames[i];
                                        com.swp.hrtms.hrtmsbe.entity.Horse horse = horseRepository.findAll().stream()
                                                        .filter(h -> hName.equals(h.getName()))
                                                        .findFirst()
                                                        .orElse(null);

                                        if (horse == null) {
                                                horse = com.swp.hrtms.hrtmsbe.entity.Horse.builder()
                                                                .name(hName)
                                                                .breed("Thoroughbred")
                                                                .owner(owner)
                                                                .age(horseAges[i])
                                                                .sex(horseSexes[i])
                                                                .weightKg(java.math.BigDecimal.valueOf(horseWeights[i]))
                                                                .status(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK)
                                                                .build();
                                                horse = horseRepository.save(horse);
                                        }

                                        com.swp.hrtms.hrtmsbe.entity.RegistrationForm form = com.swp.hrtms.hrtmsbe.entity.RegistrationForm
                                                        .builder()
                                                        .race(race)
                                                        .owner(owner)
                                                        .horse(horse)
                                                        .jockey(jockey)
                                                        .admin(admin)
                                                        .status(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING)
                                                        .createdAt(LocalDateTime.now().minusDays(1))
                                                        .build();
                                        registrationFormRepository.save(form);
                                }
                        }
                }
        }
}
// nhớ comment doan này lại ở healthCheckServiceImpl để demo,
// đoạn 178 đến 191 vì nó sẽ không có dữ liệu khi mới tạo DB
// Bypassed for demo purposes
// if (now.isAfter(raceStartDateTime.minusHours(24))) {
// throw new IllegalArgumentException(
// "Health checks must be updated no later than 24 hours before the race
// begins.");
// }

// Tương tự với PredictServiceImpl, comment doan 80-83 khi demo
// Bypassed for demo purposes
// if (now.isBefore(raceStartDateTime.minusHours(1)) ||
// !now.isBefore(raceStartDateTime)) {
// throw new IllegalArgumentException("Predictions are only allowed within 1
// hour before the race starts.");
// }
// Thoroughbred
