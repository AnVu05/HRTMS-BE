package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus;
import com.swp.hrtms.hrtmsbe.enums.RaceStatus;
import com.swp.hrtms.hrtmsbe.enums.TournamentStatus;
import com.swp.hrtms.hrtmsbe.enums.UserStatus;
import com.swp.hrtms.hrtmsbe.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class MockData {

    @Bean
    @Transactional
    public CommandLineRunner initData(
            AdminRepository adminRepository,
            UserRepository userRepository,
            RefereeRepository refereeRepository,
            SpectatorRepository spectatorRepository,
            JockeyRepository jockeyRepository,
            HorseOwnerRepository horseOwnerRepository,
            DoctorRepository doctorRepository,
            HorseRepository horseRepository,
            WalletRepository walletRepository,
            RaceFormatRepository raceFormatRepository,
            TournamentRepository tournamentRepository,
            RaceRepository raceRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                // 1. Seed Admin
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword("123456");
                admin.setEmail("admin@example.com");
                admin.setRole("ADMIN");
                admin.setStatus(UserStatus.ACTIVE);
                admin.setAvatar("https://example.com/admin-avatar.png");
                adminRepository.save(admin);
                System.out.println("Mock Admin created.");

                // 2. Seed Referee
                Referee referee = new Referee();
                referee.setUsername("referee");
                referee.setPassword("123456");
                referee.setEmail("referee@example.com");
                referee.setRole("REFEREE");
                referee.setStatus(UserStatus.ACTIVE);
                referee.setName("Trọng tài A");
                refereeRepository.save(referee);
                System.out.println("Mock Referee created.");

                // 3. Seed Spectator & Wallet
                Spectator spectator = new Spectator();
                spectator.setUsername("spectator");
                spectator.setPassword("123456");
                spectator.setEmail("spectator@example.com");
                spectator.setRole("SPECTATOR");
                spectator.setStatus(UserStatus.ACTIVE);
                spectator.setDisplayName("Khán giả B");
                spectator = spectatorRepository.save(spectator);

                Wallet wallet = Wallet.builder()
                        .user(spectator)
                        .balance(5000) // Initial balance
                        .updatedAt(LocalDateTime.now())
                        .build();
                walletRepository.save(wallet);
                System.out.println("Mock Spectator and Wallet created.");

                // 4. Seed HorseOwner
                User ownerUser = new User();
                ownerUser.setUsername("owner");
                ownerUser.setPassword("123456");
                ownerUser.setEmail("owner@example.com");
                ownerUser.setRole("HORSE_OWNER");
                ownerUser.setStatus(UserStatus.ACTIVE);
                ownerUser = userRepository.save(ownerUser);

                HorseOwner owner = HorseOwner.builder()
                        .user(ownerUser)
                        .ownerName("Chủ Ngựa C")
                        .avatar("https://example.com/owner-avatar.png")
                        .build();
                horseOwnerRepository.save(owner);
                System.out.println("Mock HorseOwner created.");

                // 5. Seed Jockey
                Jockey jockey = new Jockey();
                jockey.setUsername("jockey");
                jockey.setPassword("123456");
                jockey.setEmail("jockey@example.com");
                jockey.setRole("JOCKEY");
                jockey.setStatus(UserStatus.ACTIVE);
                jockey.setJockeyName("Nài Ngựa D");
                jockey.setExperienceYears(5);
                jockey.setAge(25);
                jockey.setProfessionalBio("Nài ngựa chuyên nghiệp");
                jockeyRepository.save(jockey);
                System.out.println("Mock Jockey created.");

                // 6. Seed Doctor
                User doctorUser = new User();
                doctorUser.setUsername("doctor");
                doctorUser.setPassword("123456");
                doctorUser.setEmail("doctor@example.com");
                doctorUser.setRole("DOCTOR");
                doctorUser.setStatus(UserStatus.ACTIVE);
                doctorUser = userRepository.save(doctorUser);

                Doctor doctor = Doctor.builder()
                        .user(doctorUser)
                        .build();
                doctorRepository.save(doctor);
                System.out.println("Mock Doctor created.");

                // 7. Seed Horse
                Horse horse = Horse.builder()
                        .owner(owner)
                        .name("Xích Thố")
                        .age(4)
                        .breed("Arabian")
                        .sex("Male")
                        .weightKg(new BigDecimal("450.00"))
                        .status(HorseStatus.WORK)
                        .build();
                horseRepository.save(horse);
                System.out.println("Mock Horse created.");

                // 8. Seed RaceFormat
                RaceFormat format = RaceFormat.builder()
                        .name("Giải Ngoại Hạng")
                        .description("Quy chuẩn đua ngựa cơ bản")
                        .entryFee(100.0)
                        .allowedHorseAge(5)
                        .maxWeight(500)
                        .status(RaceFormatStatus.ACTIVE)
                        .build();
                format = raceFormatRepository.save(format);
                System.out.println("Mock RaceFormat created.");

                // 9. Seed Tournament 1 (DRAFT)
                Tournament draftTournament = Tournament.builder()
                        .admin(admin)
                        .name("Giải Đua Ngựa Mùa Xuân 2026")
                        .createdAt(LocalDateTime.now())
                        .publishedDate(LocalDate.now().minusDays(1))
                        .openPredictionDate(LocalDate.now().plusDays(4))
                        .closePredictionDate(LocalDate.now().plusDays(5))
                        .startDate(LocalDate.now().plusDays(6))
                        .endDate(LocalDate.now().plusDays(10))
                        .status(TournamentStatus.DRAFT)
                        .build();
                tournamentRepository.save(draftTournament);
                System.out.println("Mock Draft Tournament created.");

                // 10. Seed Tournament 2 (PUBLISHED)
                Tournament publishedTournament = Tournament.builder()
                        .admin(admin)
                        .name("Giải Đua Ngựa Championship 2026")
                        .createdAt(LocalDateTime.now())
                        .publishedDate(LocalDate.now().minusDays(5))
                        .openPredictionDate(LocalDate.now().minusDays(1))
                        .closePredictionDate(LocalDate.now().plusDays(2))
                        .startDate(LocalDate.now().plusDays(3))
                        .endDate(LocalDate.now().plusDays(6))
                        .status(TournamentStatus.PUBLISHED)
                        .build();
                publishedTournament = tournamentRepository.save(publishedTournament);
                System.out.println("Mock Published Tournament created.");

                // 11. Seed Race 1 (PENDING_REFEREE) in Tournament 2
                Race racePending = Race.builder()
                        .tournament(publishedTournament)
                        .name("Vòng Loại 1 - Bảng A")
                        .date(LocalDate.now().plusDays(3))
                        .startTime(LocalTime.of(9, 0))
                        .endTime(LocalTime.of(10, 0))
                        .distanceM(1000)
                        .numHorse(8)
                        .referee(referee)
                        .status(RaceStatus.PENDING_REFEREE)
                        .raceRules(format)
                        .expectedDurationMinutes(30)
                        .breakTimeMinutes(15)
                        .build();
                raceRepository.save(racePending);
                System.out.println("Mock Pending Referee Race created.");

                // 12. Seed Race 2 (PREPARE) in Tournament 2
                Race racePrepare = Race.builder()
                        .tournament(publishedTournament)
                        .name("Vòng Loại 2 - Bảng B")
                        .date(LocalDate.now().plusDays(3))
                        .startTime(LocalTime.of(10, 30))
                        .endTime(LocalTime.of(11, 30))
                        .distanceM(1200)
                        .numHorse(8)
                        .referee(referee)
                        .status(RaceStatus.PREPARE)
                        .raceRules(format)
                        .expectedDurationMinutes(30)
                        .breakTimeMinutes(15)
                        .build();
                raceRepository.save(racePrepare);
                System.out.println("Mock Prepare Race created.");

                System.out.println("Mock Seeding for Tournament Management Flow completed successfully!");
            }
        };
    }
}
