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
                t1.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED);
                tournamentRepository.save(t1);

                com.swp.hrtms.hrtmsbe.entity.Tournament t2 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
                t2.setName("Winter Derby 2026 DRAFT");
                t2.setCreatedAt(LocalDateTime.now().minusDays(1));
                t2.setStartDate(LocalDate.now().plusMonths(3));
                t2.setEndDate(LocalDate.now().plusMonths(3).plusDays(5));
                t2.setPublishedDate(LocalDate.now().plusMonths(2));
                t2.setOpenPredictionDate(LocalDate.now().plusMonths(2).plusDays(5));
                t2.setClosePredictionDate(LocalDate.now().plusMonths(2).plusDays(25));
                t2.setAdmin(admin);
                t2.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.DRAFT);
                tournamentRepository.save(t2);

                com.swp.hrtms.hrtmsbe.entity.Tournament t3 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
                t3.setName("Spring Cup 2026 COMPLETE");
                t3.setCreatedAt(LocalDateTime.now().minusMonths(6));
                t3.setStartDate(LocalDate.now().minusMonths(4));
                t3.setEndDate(LocalDate.now().minusMonths(4).plusDays(5));
                t3.setPublishedDate(LocalDate.now().minusMonths(5));
                t3.setOpenPredictionDate(LocalDate.now().minusMonths(4).minusDays(15));
                t3.setClosePredictionDate(LocalDate.now().minusMonths(4).minusDays(2));
                t3.setAdmin(admin);
                t3.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.COMPLETE);
                tournamentRepository.save(t3);

                com.swp.hrtms.hrtmsbe.entity.Tournament t4 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
                t4.setName("Autumn Stakes 2026 CANCELLED");
                t4.setCreatedAt(LocalDateTime.now().minusMonths(2));
                t4.setStartDate(LocalDate.now().plusDays(5));
                t4.setEndDate(LocalDate.now().plusDays(10));
                t4.setPublishedDate(LocalDate.now().minusMonths(1));
                t4.setOpenPredictionDate(LocalDate.now().minusDays(15));
                t4.setClosePredictionDate(LocalDate.now().plusDays(2));
                t4.setAdmin(admin);
                t4.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.CANCELLED);
                t4.setCanceledAt(LocalDateTime.now().minusDays(2));
                t4.setCancelReason("Extreme weather conditions.");
                tournamentRepository.save(t4);

                com.swp.hrtms.hrtmsbe.entity.Tournament t5 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
                t5.setName("Grand National 2027 PUBLISHED");
                t5.setCreatedAt(LocalDateTime.now());
                t5.setStartDate(LocalDate.now().plusYears(1));
                t5.setEndDate(LocalDate.now().plusYears(1).plusDays(3));
                t5.setPublishedDate(LocalDate.now().plusMonths(6));
                t5.setOpenPredictionDate(LocalDate.now().plusMonths(7));
                t5.setClosePredictionDate(LocalDate.now().plusYears(1).minusDays(5));
                t5.setAdmin(admin);
                t5.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED);
                tournamentRepository.save(t5);

                com.swp.hrtms.hrtmsbe.entity.Tournament t6 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
                t6.setName("Test Tournament DELETE");
                t6.setCreatedAt(LocalDateTime.now().minusDays(10));
                t6.setStartDate(LocalDate.now().minusDays(5));
                t6.setEndDate(LocalDate.now().minusDays(3));
                t6.setPublishedDate(LocalDate.now().minusDays(8));
                t6.setOpenPredictionDate(LocalDate.now().minusDays(7));
                t6.setClosePredictionDate(LocalDate.now().minusDays(6));
                t6.setAdmin(admin);
                t6.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.DELETE);
                t6.setCanceledAt(LocalDateTime.now().minusDays(6));
                t6.setCancelReason("Created by mistake.");
                tournamentRepository.save(t6);

                // Tournament for current month stats
                com.swp.hrtms.hrtmsbe.entity.Tournament t7 = new com.swp.hrtms.hrtmsbe.entity.Tournament();
                t7.setName("Monthly Championship PUBLISHED");
                t7.setStartDate(LocalDate.now().withDayOfMonth(1));
                t7.setEndDate(LocalDate.now().withDayOfMonth(28));
                t7.setPublishedDate(LocalDate.now().withDayOfMonth(1).minusDays(5));
                t7.setAdmin(admin);
                t7.setStatus(com.swp.hrtms.hrtmsbe.enums.TournamentStatus.PUBLISHED);
                tournamentRepository.save(t7);

                // 5. Create Race PENDING_REFEREE
                com.swp.hrtms.hrtmsbe.entity.Race r1 = new com.swp.hrtms.hrtmsbe.entity.Race();
                r1.setTournament(t1);
                r1.setName("Race 1 - Qualifier");
                r1.setRaceRules(format1);
                r1.setDate(LocalDate.now().plusDays(11));
                r1.setStartTime(LocalTime.of(10, 0));
                r1.setEndTime(LocalTime.of(10, 30));
                r1.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED);
                r1.setNumHorse(8);
                raceRepository.save(r1);

                com.swp.hrtms.hrtmsbe.entity.Race r2 = new com.swp.hrtms.hrtmsbe.entity.Race();
                r2.setTournament(t7);
                r2.setName("Race 2 - Sprint");
                r2.setRaceRules(format1);
                r2.setDate(LocalDate.now().withDayOfMonth(5));
                r2.setStartTime(LocalTime.of(14, 0));
                r2.setEndTime(LocalTime.of(14, 30));
                r2.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.COMPLETE);
                r2.setNumHorse(10);
                raceRepository.save(r2);

                com.swp.hrtms.hrtmsbe.entity.Race r3 = new com.swp.hrtms.hrtmsbe.entity.Race();
                r3.setTournament(t7);
                r3.setName("Race 3 - Endurance");
                r3.setRaceRules(format1);
                r3.setDate(LocalDate.now().withDayOfMonth(15));
                r3.setStartTime(LocalTime.of(15, 0));
                r3.setEndTime(LocalTime.of(15, 30));
                r3.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.COMPLETE);
                r3.setNumHorse(12);
                raceRepository.save(r3);

                com.swp.hrtms.hrtmsbe.entity.Race r4 = new com.swp.hrtms.hrtmsbe.entity.Race();
                r4.setTournament(t7);
                r4.setName("Race 4 - Final");
                r4.setRaceRules(format1);
                r4.setDate(LocalDate.now().withDayOfMonth(25));
                r4.setStartTime(LocalTime.of(16, 0));
                r4.setEndTime(LocalTime.of(16, 30));
                r4.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.PUBLISHED);
                r4.setNumHorse(12);
                raceRepository.save(r4);
                // 6. Create Jockey
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

                // 6.5 Create Jockey Cert
                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert1 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
                cert1.setCertName("Professional Riding License");
                cert1.setCertImageBase64("R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7");

                cert1.setStatus(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.PENDING);
                cert1.setJockey(jockey1);
                jockeyCertRepository.save(cert1);

                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert2 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
                cert2.setCertName("Advanced Riding License");
                // A tiny 1x1 transparent GIF base64 string to keep mock data small
                cert2.setCertImageBase64("ádasdasd");
                cert2.setIssuedAt(LocalDate.now().minusMonths(6));
                cert2.setStatus(com.swp.hrtms.hrtmsbe.enums.CertificateStatus.PENDING);
                cert2.setJockey(jockey1);
                jockeyCertRepository.save(cert2);

                // 6.6 Send verification notification to Admin (matches VerificationServiceImpl)
                com.swp.hrtms.hrtmsbe.entity.Notification certNotif = com.swp.hrtms.hrtmsbe.entity.Notification
                                .builder()
                                .sender(jockey1)
                                .title("Certificate Verification Request")
                                .content("A jockey has requested verification for all pending certificates.")
                                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.VERIFI_CERTIFICATE)
                                // Note: The real API does not link the specific certificate (jockeyCert) here
                                .build();
                notificationRepository.save(certNotif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient certNotifRec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                                .builder()
                                .notification(certNotif)
                                .recipient(admin)
                                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                                .build();
                notificationRecipientRepository.save(certNotifRec);

                // 7. Create Owner
                com.swp.hrtms.hrtmsbe.entity.HorseOwner owner1 = new com.swp.hrtms.hrtmsbe.entity.HorseOwner();
                com.swp.hrtms.hrtmsbe.entity.User ownerUser = new com.swp.hrtms.hrtmsbe.entity.User();
                ownerUser.setUsername("owner_mock");
                ownerUser.setPassword("123456");
                ownerUser.setEmail("vudinhan2k5@gmail.com");
                ownerUser.setRole("HORSE_OWNER");
                ownerUser.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
                ownerUser = userRepository.save(ownerUser);
                owner1.setUser(ownerUser);
                owner1.setOwnerName("Mock Owner");
                horseOwnerRepository.save(owner1);

                // 8. Create Horse
                com.swp.hrtms.hrtmsbe.entity.Horse horse1 = new com.swp.hrtms.hrtmsbe.entity.Horse();
                horse1.setName("Mock Horse");
                horse1.setSex("M");
                horse1.setAge(3);
                horse1.setBreed("Thoroughbred");
                horse1.setWeightKg(BigDecimal.valueOf(450.0));
                horse1.setOwner(owner1);
                horse1.setStatus(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK);
                horseRepository.save(horse1);

                // Add 5 more horses for owner1 (which is ID = 5)
                com.swp.hrtms.hrtmsbe.entity.Horse horse2 = new com.swp.hrtms.hrtmsbe.entity.Horse();
                horse2.setName("Thunder Strike");
                horse2.setSex("M");
                horse2.setAge(4);
                horse2.setBreed("Arabian");
                horse2.setWeightKg(BigDecimal.valueOf(460.5));
                horse2.setOwner(owner1);
                horse2.setStatus(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK);
                horseRepository.save(horse2);

                com.swp.hrtms.hrtmsbe.entity.Horse horse3 = new com.swp.hrtms.hrtmsbe.entity.Horse();
                horse3.setName("Shadow Fax");
                horse3.setSex("F");
                horse3.setAge(5);
                horse3.setBreed("Mustang");
                horse3.setWeightKg(BigDecimal.valueOf(430.0));
                horse3.setOwner(owner1);
                horse3.setStatus(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK);
                horseRepository.save(horse3);

                com.swp.hrtms.hrtmsbe.entity.Horse horse4 = new com.swp.hrtms.hrtmsbe.entity.Horse();
                horse4.setName("Lightning Flash");
                horse4.setSex("M");
                horse4.setAge(3);
                horse4.setBreed("Quarter Horse");
                horse4.setWeightKg(BigDecimal.valueOf(480.2));
                horse4.setOwner(owner1);
                horse4.setStatus(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK);
                horseRepository.save(horse4);

                com.swp.hrtms.hrtmsbe.entity.Horse horse5 = new com.swp.hrtms.hrtmsbe.entity.Horse();
                horse5.setName("Star Dancer");
                horse5.setSex("F");
                horse5.setAge(6);
                horse5.setBreed("Appaloosa");
                horse5.setWeightKg(BigDecimal.valueOf(445.8));
                horse5.setOwner(owner1);
                horse5.setStatus(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK);
                horseRepository.save(horse5);

                com.swp.hrtms.hrtmsbe.entity.Horse horse6 = new com.swp.hrtms.hrtmsbe.entity.Horse();
                horse6.setName("Midnight Runner");
                horse6.setSex("M");
                horse6.setAge(4);
                horse6.setBreed("Friesian");
                horse6.setWeightKg(BigDecimal.valueOf(490.0));
                horse6.setOwner(owner1);
                horse6.setStatus(com.swp.hrtms.hrtmsbe.entity.HorseStatus.WORK);
                horseRepository.save(horse6);

                // 9. Create Registration Form
                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form1 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form1.setTournament(t1);
                form1.setAdmin(admin);
                form1.setRace(r1);
                form1.setHorse(horse1);
                form1.setJockey(jockey1);
                form1.setOwner(owner1);
                form1.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_ADMIN);
                form1 = registrationFormRepository.save(form1);

                // Create a second Registration Form with RACING status
                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form2 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form2.setTournament(t1);
                form2.setAdmin(admin);
                form2.setRace(r1);
                form2.setHorse(horse1);
                form2.setJockey(jockey1);
                form2.setOwner(owner1);
                form2.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
                form2 = registrationFormRepository.save(form2);

                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form3 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form3.setTournament(t1);
                form3.setAdmin(admin);
                form3.setRace(r1);
                form3.setHorse(horse2);
                form3.setJockey(jockey1);
                form3.setOwner(owner1);
                form3.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
                form3 = registrationFormRepository.save(form3);

                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form4 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form4.setTournament(t1);
                form4.setAdmin(admin);
                form4.setRace(r1);
                form4.setHorse(horse3);
                form4.setJockey(jockey1);
                form4.setOwner(owner1);
                form4.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
                form4 = registrationFormRepository.save(form4);

                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form5 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form5.setTournament(t1);
                form5.setAdmin(admin);
                form5.setRace(r1);
                form5.setHorse(horse4);
                form5.setJockey(jockey1);
                form5.setOwner(owner1);
                form5.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
                form5 = registrationFormRepository.save(form5);

                // Add Approved forms for Stats (Fill rate)
                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form6 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form6.setTournament(t7);
                form6.setAdmin(admin);
                form6.setRace(r2);
                form6.setHorse(horse2);
                form6.setJockey(jockey1);
                form6.setOwner(owner1);
                form6.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
                registrationFormRepository.save(form6);

                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form7 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form7.setTournament(t7);
                form7.setAdmin(admin);
                form7.setRace(r2);
                form7.setHorse(horse3);
                form7.setJockey(jockey1);
                form7.setOwner(owner1);
                form7.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
                registrationFormRepository.save(form7);

                com.swp.hrtms.hrtmsbe.entity.RegistrationForm form8 = new com.swp.hrtms.hrtmsbe.entity.RegistrationForm();
                form8.setTournament(t7);
                form8.setAdmin(admin);
                form8.setRace(r3);
                form8.setHorse(horse4);
                form8.setJockey(jockey1);
                form8.setOwner(owner1);
                form8.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
                registrationFormRepository.save(form8);

                // 10. Create 5 Health Checks with doctor1 (id = 3)
                com.swp.hrtms.hrtmsbe.entity.HealthCheck hc1 = new com.swp.hrtms.hrtmsbe.entity.HealthCheck();
                hc1.setRegistrationForm(form1);
                hc1.setDoctor(doctor1);
                hc1.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.PENDING_DOCTOR);
                hc1.setMedicalNotes("Waiting for doctor assignment");
                healthCheckRepository.save(hc1);

                com.swp.hrtms.hrtmsbe.entity.HealthCheck hc2 = new com.swp.hrtms.hrtmsbe.entity.HealthCheck();
                hc2.setRegistrationForm(form2);
                hc2.setDoctor(doctor1);
                hc2.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.DOCTOR_INVITED);
                hc2.setMedicalNotes("Doctor has been invited to check");
                healthCheckRepository.save(hc2);

                com.swp.hrtms.hrtmsbe.entity.HealthCheck hc3 = new com.swp.hrtms.hrtmsbe.entity.HealthCheck();
                hc3.setRegistrationForm(form3);
                hc3.setDoctor(doctor1);
                hc3.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.CHECKING);
                hc3.setCheckDate(LocalDateTime.now().minusDays(1));
                hc3.setMedicalNotes("Undergoing medical examination");
                healthCheckRepository.save(hc3);

                com.swp.hrtms.hrtmsbe.entity.HealthCheck hc4 = new com.swp.hrtms.hrtmsbe.entity.HealthCheck();
                hc4.setRegistrationForm(form4);
                hc4.setDoctor(doctor1);
                hc4.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.ACCEPT);
                hc4.setCheckDate(LocalDateTime.now().minusDays(2));
                hc4.setMedicalNotes("Horse is fully healthy and fit for the race");
                healthCheckRepository.save(hc4);

                com.swp.hrtms.hrtmsbe.entity.HealthCheck hc5 = new com.swp.hrtms.hrtmsbe.entity.HealthCheck();
                hc5.setRegistrationForm(form5);
                hc5.setDoctor(doctor1);
                hc5.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.REJECT);
                hc5.setCheckDate(LocalDateTime.now().minusDays(3));
                hc5.setMedicalNotes("Horse has minor injury, not fit for the race");
                healthCheckRepository.save(hc5);

        }
}
