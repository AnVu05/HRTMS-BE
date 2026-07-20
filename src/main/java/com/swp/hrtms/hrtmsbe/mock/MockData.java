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
                        jockey1.setUsername("jockey_mock");
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
                        ownerUser.setUsername("owner_mock");
                        ownerUser.setPassword("123456");
                        ownerUser.setEmail("owner_mock@hrtms.com");
                        ownerUser.setRole("HORSE_OWNER");
                        ownerUser.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                        ownerUser = userRepository.save(ownerUser);
                        owner1.setUser(ownerUser);
                        owner1.setOwnerName("Mock Owner");
                        horseOwnerRepository.save(owner1);
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
                                // Create RaceFormat
                                RaceFormat format = RaceFormat.builder()
                                                .name("Demo Thoroughbred Format")
                                                .description("Valid rules for mock thoroughbreds")
                                                .entryFee(100.0)
                                                .firstPrizePercent(50.0)
                                                .secondPrizePercent(30.0)
                                                .thirdPrizePercent(20.0)
                                                .allowedBreed("Thoroughbred")
                                                .allowedHorseAge(4) // Cho phep ngua tu 4 tuoi tro len (minh co ngua 5,
                                                                    // 6)
                                                .minJockeyExperience(2) // Cho phep nai co 2 nam KN tro len (minh co 3,
                                                                        // 4, 5)
                                                .minWeight(0)
                                                .maxWeight(1000)
                                                .baseWeight(50)
                                                .applyFemaleAllowance(0)
                                                .status(com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus.ACTIVE)
                                                .build();
                                format = raceFormatRepository.save(format);

                                // Create Tournament
                                Tournament tournament = Tournament.builder()
                                                .name("Demo Grand Prix")
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

                                // Create Race
                                Race race = Race.builder()
                                                .tournament(tournament)
                                                .name("Demo Grand Race 1")
                                                .date(LocalDate.now())
                                                .startTime(LocalTime.now().plusMinutes(10)) // Bat dau trong 10 phut nua
                                                                                            // (hop le cho du doan)
                                                .endTime(LocalTime.now().plusMinutes(40))
                                                .numHorse(10)
                                                .distanceM(1200)
                                                .referee(referee)
                                                .status(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED)
                                                .raceRules(format)
                                                .expectedDurationMinutes(30)
                                                .breakTimeMinutes(10)
                                                .build();
                                raceRepository.save(race);
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
