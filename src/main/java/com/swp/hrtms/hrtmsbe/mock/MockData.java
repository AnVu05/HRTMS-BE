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

    @jakarta.annotation.PostConstruct
    @Transactional
    public void init() {
        if (userRepository.count() > 0)
            return;

        // 1. Create Admin
        com.swp.hrtms.hrtmsbe.entity.Admin admin = new com.swp.hrtms.hrtmsbe.entity.Admin();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setEmail("admin@hrtms.com");
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

        // 3. Create RaceFormat
        com.swp.hrtms.hrtmsbe.entity.RaceFormat format1 = new com.swp.hrtms.hrtmsbe.entity.RaceFormat();
        format1.setName("Derby 1000m");
        format1.setDescription("Standard Derby");
        format1.setEntryFee(100.0);
        format1.setFirstPrizePercent(50.0);
        format1.setSecondPrizePercent(30.0);
        format1.setThirdPrizePercent(20.0);
        format1.setAllowedHorseAge(3);
        format1.setMinJockeyExperience(1);
        format1.setMinWeight(40);
        format1.setMaxWeight(60);
        format1.setBaseWeight(50);
        format1.setApplyFemaleAllowance(1);
        raceFormatRepository.save(format1);

        // 4. Create Tournament DRAFT
        com.swp.hrtms.hrtmsbe.entity.Tournament t1 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
        t1.setName("Summer Cup 2026 DRAFT");
        t1.setStartDate(LocalDate.now().plusDays(10));
        t1.setEndDate(LocalDate.now().plusDays(20));
        t1.setPublishedDate(LocalDate.now().plusDays(1));
        t1.setOpenPredictionDate(LocalDate.now().plusDays(2));
        t1.setClosePredictionDate(LocalDate.now().plusDays(8));
        t1.setAdmin(admin);
        t1.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.DRAFT);
        tournamentRepository.save(t1);

        // 5. Create Race PENDING_REFEREE
        com.swp.hrtms.hrtmsbe.entity.Race r1 = new com.swp.hrtms.hrtmsbe.entity.Race();
        r1.setTournament(t1);
        r1.setName("Race 1 - Qualifier");
        r1.setRaceRules(format1);
        r1.setDate(LocalDate.now().plusDays(11));
        r1.setStartTime(LocalTime.of(10, 0));
        r1.setEndTime(LocalTime.of(10, 30));
        r1.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE);
        raceRepository.save(r1);
    }
}