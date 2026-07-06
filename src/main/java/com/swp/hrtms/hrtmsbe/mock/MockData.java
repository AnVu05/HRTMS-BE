package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.*;
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

        private final UserRepository userRepository;
        private final AdminRepository adminRepository;
        private final SpectatorRepository spectatorRepository;
        private final HorseOwnerRepository horseOwnerRepository;
        private final JockeyRepository jockeyRepository;
        private final RefereeRepository refereeRepository;
        private final TournamentRepository tournamentRepository;
        private final RaceRepository raceRepository;
        private final HorseRepository horseRepository;
        private final WalletRepository walletRepository;
        private final RegistrationFormRepository registrationFormRepository;
        private final RaceFormatRepository raceFormatRepository;
        private final PredictionRepository predictionRepository;
        private final TransactionRepository transactionRepository;

        @PostConstruct
        @Transactional
        public void init() {
                if (userRepository.count() > 0) {
                        return; // Tránh việc mỗi lần restart server lại tự động sinh thêm 1 bộ dữ liệu mới!
                }
                String suffix = "_" + System.currentTimeMillis();

                // 1. Tạo các User (để ID tự tăng)
                Admin admin = new Admin();
                admin.setUsername("admin" + suffix);
                admin.setPassword("password");
                admin.setEmail("admin" + suffix + "@example.com");
                admin.setRole("ADMIN");
                admin = adminRepository.save(admin);

                Spectator spectator = new Spectator();
                spectator.setUsername("spectator" + suffix);
                spectator.setPassword("password");
                spectator.setEmail("spectator" + suffix + "@example.com");
                spectator.setRole("SPECTATOR");
                spectator.setDisplayName("Mock Spectator");
                spectator = spectatorRepository.save(spectator);

                // Tạo ví cho Spectator
                Wallet wallet = Wallet.builder()
                                .userId(spectator.getId())
                                .balance(100000)
                                .updatedAt(LocalDateTime.now())
                                .build();
                walletRepository.save(wallet);

                User ownerUser = new User();
                ownerUser.setUsername("owner" + suffix);
                ownerUser.setPassword("password");
                ownerUser.setEmail("owner" + suffix + "@example.com");
                ownerUser.setRole("HORSE_OWNER");
                ownerUser = userRepository.save(ownerUser);

                HorseOwner owner = new HorseOwner();
                owner.setUser(ownerUser);
                owner = horseOwnerRepository.save(owner);

                Jockey jockey = new Jockey();
                jockey.setUsername("jockey" + suffix);
                jockey.setPassword("password");
                jockey.setEmail("jockey" + suffix + "@example.com");
                jockey.setRole("JOCKEY");
                jockey = jockeyRepository.save(jockey);

                Referee referee = new Referee();
                referee.setUsername("referee" + suffix);
                referee.setPassword("password");
                referee.setEmail("referee" + suffix + "@example.com");
                referee.setRole("REFEREE");
                referee.setName("Mock Referee");
                referee = refereeRepository.save(referee);

                // 2. Tạo Horse cho HorseOwner
                Horse horse1 = Horse.builder()
                                .name("Thunderbolt" + suffix)
                                .age(5)
                                .breed("Arabian")
                                .status(HorseStatus.WORKED)
                                .owner(owner)
                                .build();
                horse1 = horseRepository.save(horse1);

                // 3. Tạo Tournament (gán id Admin vừa tạo)
                Tournament tournament = Tournament.builder()
                                .admin(admin)
                                .name("Spring Championship" + suffix)
                                .startDate(LocalDate.now().minusDays(1))
                                .endDate(LocalDate.now().plusDays(10))
                                .publishedDate(LocalDate.now().minusDays(2))
                                .openPredictionDate(LocalDate.now().minusDays(1))
                                .closePredictionDate(LocalDate.now().plusDays(10))
                                .status(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED)
                                .build();
                tournament = tournamentRepository.save(tournament);

                // 4. Tạo RaceFormat (Lưu ý: Entity RaceFormat hiện tại không có trường Admin
                // theo DB thiết kế)
                RaceFormat format = RaceFormat.builder()
                                .name("Standard Format" + suffix)
                                .description("Standard Race Rules")
                                .entryFee(100.0)
                                .firstPrizePercent(50.0)
                                .secondPrizePercent(30.0)
                                .thirdPrizePercent(20.0)
                                .allowedBreed("Arabian")
                                .allowedHorseAge(5)
                                .minJockeyExperience(2)
                                .minWeight(40)
                                .maxWeight(80)
                                .baseWeight(50)
                                .applyFemaleAllowance(2)
                                .status(com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus.ACTIVE)
                                .build();
                format = raceFormatRepository.save(format);

                // 5. Tạo Race (gán Tournament, RaceFormat và Referee)
                Race race = Race.builder()
                                .tournament(tournament)
                                .name("Final Sprint" + suffix)
                                .date(LocalDate.now())
                                .startTime(LocalTime.now().plusMinutes(60))
                                .endTime(LocalTime.now().plusMinutes(120))
                                .distanceM(1000)
                                .horseBreed("Arabian")
                                .weightKg(new BigDecimal("500"))
                                .horseAge(5)
                                .bettingReward(5000L)
                                .raceRules(format)
                                .referee(referee)
                                .status(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED)
                                .build();
                race = raceRepository.save(race);

                // 6. Tạo Registration Forms
                RegistrationForm form1 = RegistrationForm.builder()
                                .owner(owner)
                                .horse(horse1)
                                .jockey(jockey)
                                .tournament(tournament)
                                .race(race)
                                .admin(admin)
                                .status(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PREPARE)
                                .createdAt(LocalDateTime.now())
                                .build();
                registrationFormRepository.save(form1);

                // 7. Tạo Prediction và Transaction để test hoàn tiền
                Prediction prediction = Prediction.builder()
                                .spectator(spectator)
                                .race(race)
                                .predictedHorse(horse1)
                                .pointsInvested(10000)
                                .status(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING)
                                .createdAt(LocalDateTime.now())
                                .build();
                predictionRepository.save(prediction);

                Transaction transaction = Transaction.builder()
                                .wallet(wallet)
                                .tournament(tournament)
                                .race(race)
                                .horse(horse1)
                                .amount(-10000)
                                .type("PREDICTION_DEDUCT")
                                .createdAt(LocalDateTime.now())
                                .build();
                transactionRepository.save(transaction);
        }
}