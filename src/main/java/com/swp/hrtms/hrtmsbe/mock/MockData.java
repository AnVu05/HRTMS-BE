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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MockData {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final SpectatorRepository spectatorRepository;
    private final HorseOwnerRepository horseOwnerRepository;
    private final TournamentRepository tournamentRepository;
    private final RaceRepository raceRepository;
    private final HorseRepository horseRepository;
    private final WalletRepository walletRepository;

    @PostConstruct
    @Transactional
    public void init() {
        // Create Admin
        Admin admin = null;
        if (!userRepository.existsByUsername("admin_mock")) {
            admin = new Admin();
            admin.setUsername("admin_mock");
            admin.setPassword("password");
            admin.setEmail("admin_mock@example.com");
            admin.setRole("ADMIN");
            admin = adminRepository.save(admin);
        } else {
            Optional<User> u = userRepository.findByUsername("admin_mock");
            if (u.isPresent() && u.get() instanceof Admin) {
                admin = (Admin) u.get();
            }
        }

        // Create Spectator
        Spectator spectator = null;
        if (!userRepository.existsByUsername("spectator_mock")) {
            spectator = new Spectator();
            spectator.setUsername("spectator_mock");
            spectator.setPassword("password");
            spectator.setEmail("spectator_mock@example.com");
            spectator.setRole("SPECTATOR");
            spectator.setDisplayName("Mock Spectator");
            spectator = spectatorRepository.save(spectator);

            // Create Wallet for Spectator
            Wallet wallet = Wallet.builder()
                    .userId(spectator.getId())
                    .balance(100000) // 100k points
                    .updatedAt(LocalDateTime.now())
                    .build();
            walletRepository.save(wallet);
        } else {
            Optional<User> u = userRepository.findByUsername("spectator_mock");
            if (u.isPresent() && u.get() instanceof Spectator) {
                spectator = (Spectator) u.get();
            }
        }

        // Create HorseOwner
        HorseOwner owner = null;
        if (!userRepository.existsByUsername("owner_mock")) {
            User ownerUser = new User();
            ownerUser.setUsername("owner_mock");
            ownerUser.setPassword("password");
            ownerUser.setEmail("owner_mock@example.com");
            ownerUser.setRole("HORSE_OWNER");
            ownerUser = userRepository.save(ownerUser);

            owner = new HorseOwner();
            owner.setUser(ownerUser);
            owner = horseOwnerRepository.save(owner);
        } else {
            Optional<User> u = userRepository.findByUsername("owner_mock");
            if (u.isPresent()) {
                owner = horseOwnerRepository.findById(u.get().getId()).orElse(null);
            }
        }

        // Create Horse
        if (horseRepository.count() == 0 && owner != null) {
            Horse horse1 = Horse.builder()
                    .name("Thunderbolt")
                    .age(5)
                    .breed("Arabian")
                    .status(HorseStatus.WORKED)
                    .owner(owner)
                    .build();
            horseRepository.save(horse1);

            Horse horse2 = Horse.builder()
                    .name("Lightning")
                    .age(4)
                    .breed("Thoroughbred")
                    .status(HorseStatus.WORKED)
                    .owner(owner)
                    .build();
            horseRepository.save(horse2);
        }

        // Create Tournament and Race
        if (tournamentRepository.count() == 0 && admin != null) {
            Tournament tournament = Tournament.builder()
                    .admin(admin)
                    .name("Spring Championship")
                    .startDate(LocalDate.now().minusDays(1))
                    .endDate(LocalDate.now().plusDays(10))
                    .publishedDate(LocalDate.now().minusDays(2))
                    .openPredictionDate(LocalDate.now().minusDays(1))
                    .closePredictionDate(LocalDate.now().plusDays(10))
                    .status(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED)
                    .build();
            tournament = tournamentRepository.save(tournament);

            // Create Race starting in 30 minutes to allow prediction (within 1 hour)
            Race race = Race.builder()
                    .tournament(tournament)
                    .name("Final Sprint")
                    .date(LocalDate.now())
                    .startTime(LocalTime.now().plusMinutes(30))
                    .endTime(LocalTime.now().plusMinutes(90))
                    .distanceM(1000)
                    .horseBreed("Arabian")
                    .weightKg(new BigDecimal("500"))
                    .horseAge(5)
                    .bettingReward(5000L)
                    .status(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE)
                    .build();
            raceRepository.save(race);
        }
    }
}