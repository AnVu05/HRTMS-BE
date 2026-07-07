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
    private final RaceFormatRepository raceFormatRepository;
    private final RaceRepository raceRepository;
    private final HorseRepository horseRepository;

    @PostConstruct
    @Transactional
    public void init() {
        if (userRepository.count() > 0)
            return; // Only init if empty
        String sfx = "_" + System.currentTimeMillis();

        // Admin
        Admin admin = new Admin();
        admin.setUsername("admin" + sfx);
        admin.setPassword("password");
        admin.setEmail("admin" + sfx + "@example.com");
        admin.setRole("ADMIN");
        adminRepository.save(admin);

        // Spectator
        Spectator spec = new Spectator();
        spec.setUsername("spectator" + sfx);
        spec.setPassword("password");
        spec.setEmail("spectator" + sfx + "@example.com");
        spec.setRole("SPECTATOR");
        spec.setDisplayName("Mock Spectator");
        spectatorRepository.save(spec);

        // Horse Owner
        User ownerUser = new User();
        ownerUser.setUsername("owner" + sfx);
        ownerUser.setPassword("password");
        ownerUser.setEmail("owner" + sfx + "@example.com");
        ownerUser.setRole("HORSE_OWNER");
        userRepository.save(ownerUser);

        HorseOwner owner = new HorseOwner();
        owner.setUser(ownerUser);
        horseOwnerRepository.save(owner);

        // Referee
        Referee ref = new Referee();
        ref.setUsername("referee" + sfx);
        ref.setPassword("password");
        ref.setEmail("referee" + sfx + "@example.com");
        ref.setRole("REFEREE");
        ref.setName("Mock Referee");
        refereeRepository.save(ref);

        // Jockey 1: Has matching cert (Arabian)
        Jockey j1 = new Jockey();
        j1.setUsername("jockey1" + sfx);
        j1.setPassword("password");
        j1.setEmail("j1" + sfx + "@example.com");
        j1.setRole("JOCKEY");
        j1.setJockeyName("Jockey One");
        j1.setExperienceYears(5); // Meets rules
        j1.setAge(25);
        j1.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);

        JockeyCert cert1 = new JockeyCert();
        cert1.setCertName("Arabian"); // Matching breed
        cert1.setStatus("VERIFIED");
        cert1.setJockey(j1);
        j1.setJockeyCerts(java.util.Arrays.asList(cert1));
        jockeyRepository.save(j1);

        // Jockey 2: No certs
        Jockey j2 = new Jockey();
        j2.setUsername("jockey2" + sfx);
        j2.setPassword("password");
        j2.setEmail("j2" + sfx + "@example.com");
        j2.setRole("JOCKEY");
        j2.setJockeyName("Jockey Two");
        j2.setExperienceYears(5);
        j2.setAge(26);
        j2.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
        jockeyRepository.save(j2);

        // Jockey 3: Experience less than required (1 < 3)
        Jockey j3 = new Jockey();
        j3.setUsername("jockey3" + sfx);
        j3.setPassword("password");
        j3.setEmail("j3" + sfx + "@example.com");
        j3.setRole("JOCKEY");
        j3.setJockeyName("Jockey Three");
        j3.setExperienceYears(1); // Less than required (3)
        j3.setAge(22);
        j3.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);

        JockeyCert cert3 = new JockeyCert();
        cert3.setCertName("Arabian");
        cert3.setStatus("VERIFIED");
        cert3.setJockey(j3);
        j3.setJockeyCerts(java.util.Arrays.asList(cert3));
        jockeyRepository.save(j3);

        // Tournament PUBLISHED
        Tournament tour = Tournament.builder()
                .admin(admin)
                .name("Mock Tournament" + sfx)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .status(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED)
                .build();
        tournamentRepository.save(tour);

        // RaceFormat
        RaceFormat format = RaceFormat.builder()
                .name("Mock Rules")
                .minJockeyExperience(3)
                .allowedBreed("Arabian")
                .allowedHorseAge(6)
                .build();
        raceFormatRepository.save(format);

        // Race (PREPARE, numHorse = 3)
        Race race = Race.builder()
                .tournament(tour)
                .name("Mock Race" + sfx)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .numHorse(3)
                .distanceM(1000)
                .referee(ref)
                .raceRules(format)
                .status(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PREPARE)
                .build();
        raceRepository.save(race);

        // Horse 1: breed matches cert of Jockey 1 and Race Rules
        Horse h1 = Horse.builder()
                .name("Arabian") // "có tên trùng với chứng chỉ của nài ngựa 1"
                .breed("Arabian")
                .age(5)
                .sex("Male")
                .weightKg(BigDecimal.valueOf(400))
                .status(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORKED)
                .owner(owner)
                .build();
        horseRepository.save(h1);

        // Horse 2: breed does not match cert
        Horse h2 = Horse.builder()
                .name("Thoroughbred")
                .breed("Thoroughbred")
                .age(5)
                .sex("Male")
                .weightKg(BigDecimal.valueOf(400))
                .status(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORKED)
                .owner(owner)
                .build();
        horseRepository.save(h2);

        // Horse 3: breed and age do not match race rules
        Horse h3 = Horse.builder()
                .name("Old Pony")
                .breed("Pony") // doesn't match Arabian
                .age(10) // > 6 (doesn't match allowedHorseAge=6)
                .sex("Male")
                .weightKg(BigDecimal.valueOf(400))
                .status(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORKED)
                .owner(owner)
                .build();
        horseRepository.save(h3);
    }
}