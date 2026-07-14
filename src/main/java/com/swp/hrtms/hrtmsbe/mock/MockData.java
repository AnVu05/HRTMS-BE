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
                if (userRepository.count() > 0)
                        return;

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

                // Create RaceFormat
                com.swp.hrtms.hrtmsbe.entity.RaceFormat format1 = new com.swp.hrtms.hrtmsbe.entity.RaceFormat();
                format1.setName("Derby 1000m");
                format1.setDescription("Standard Derby");
                format1.setEntryFee(100.0);
                format1.setFirstPrizePercent(50.0);
                format1.setSecondPrizePercent(30.0);
                format1.setThirdPrizePercent(20.0);
                format1.setAllowedBreed("Thoroughbred");
                format1.setAllowedHorseAge(3);
                format1.setMinJockeyExperience(1);
                format1.setMinWeight(40);
                format1.setMaxWeight(60);
                format1.setBaseWeight(50);
                format1.setApplyFemaleAllowance(1);
                raceFormatRepository.save(format1);

                // Create Tournament PUBLISHED
                com.swp.hrtms.hrtmsbe.entity.Tournament t1 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
                t1.setName("Summer Cup 2026");
                t1.setStartDate(java.time.LocalDate.now());
                t1.setEndDate(java.time.LocalDate.now().plusDays(1));
                t1.setPublishedDate(java.time.LocalDate.now().minusDays(2));
                t1.setOpenPredictionDate(java.time.LocalDate.now().minusDays(1));
                t1.setClosePredictionDate(java.time.LocalDate.now().plusDays(1));
                t1.setAdmin(admin);
                t1.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED);
                tournamentRepository.save(t1);

                // Create Race PUBLISHED
                com.swp.hrtms.hrtmsbe.entity.Race r1 = new com.swp.hrtms.hrtmsbe.entity.Race();
                r1.setTournament(t1);
                r1.setName("Race 1 - Qualifier");
                r1.setRaceRules(format1);
                r1.setDate(java.time.LocalDate.now());
                r1.setStartTime(java.time.LocalTime.now().plusHours(1));
                r1.setEndTime(java.time.LocalTime.now().plusHours(1).plusMinutes(30));
                r1.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PREPARE);
                r1.setNumHorse(8);
                r1.setReferee(referee1);
                raceRepository.save(r1);

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

                // Create Jockey Cert
                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert1 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
                cert1.setCertName("Thoroughbred");
                cert1.setCertImageBase64("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7");
                cert1.setStatus(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.VERIFIED);
                cert1.setJockey(jockey1);
                jockeyCertRepository.save(cert1);

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

                // Create Horse for Owner
                com.swp.hrtms.hrtmsbe.entity.Horse horse1 = new com.swp.hrtms.hrtmsbe.entity.Horse();
                horse1.setName("Mock Thoroughbred");
                horse1.setSex("M");
                horse1.setAge(3);
                horse1.setBreed("Thoroughbred");
                horse1.setWeightKg(java.math.BigDecimal.valueOf(450.0));
                horse1.setOwner(owner1);
                horse1.setStatus(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK);
                horseRepository.save(horse1);

                // Create Spectator
                com.swp.hrtms.hrtmsbe.entity.Spectator spectator1 = new com.swp.hrtms.hrtmsbe.entity.Spectator();
                spectator1.setUsername("spectator1");
                spectator1.setPassword("123456");
                spectator1.setEmail("spectator1@hrtms.com");
                spectator1.setRole("SPECTATOR");
                spectator1.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                spectatorRepository.save(spectator1);

                com.swp.hrtms.hrtmsbe.entity.Wallet spectatorWallet = new com.swp.hrtms.hrtmsbe.entity.Wallet();
                spectatorWallet.setUser(spectator1);
                spectatorWallet.setBalance(1000);
                spectatorWallet.setUpdatedAt(LocalDateTime.now());
                walletRepository.save(spectatorWallet);

                // Create RegistrationForm with RACING status
                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form1 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form1.setTournament(t1);
                form1.setAdmin(admin);
                form1.setRace(r1);
                form1.setHorse(horse1);
                form1.setJockey(jockey1);
                form1.setOwner(owner1);
                form1.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
                registrationFormRepository.save(form1);

                // Create Prediction
                com.swp.hrtms.hrtmsbe.entity.Prediction prediction1 = new com.swp.hrtms.hrtmsbe.entity.Prediction();
                prediction1.setSpectator(spectator1);
                prediction1.setRace(r1);
                prediction1.setPredictedHorse(horse1);
                prediction1.setPointsInvested(100);
                prediction1.setStatus(com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING);
                prediction1.setCreatedAt(java.time.LocalDateTime.now());
                predictionRepository.save(prediction1);
        }
}
