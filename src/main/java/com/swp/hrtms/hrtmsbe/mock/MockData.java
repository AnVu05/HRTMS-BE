package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.entity.UserRole;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class MockData {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final TournamentRepository tournamentRepository;
    private final RaceRepository raceRepository;
    private final com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository jockeyCertRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository;

    public MockData(UserRepository userRepository,
            AdminRepository adminRepository,
            TournamentRepository tournamentRepository,
            RaceRepository raceRepository,
            com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository jockeyCertRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.tournamentRepository = tournamentRepository;
        this.raceRepository = raceRepository;
        this.jockeyCertRepository = jockeyCertRepository;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    public void generateData() {
        // Check if data already exists to avoid duplicate data on application restart
        if (userRepository.count() > 0) {
            System.out.println("Data already exists. Skipping test data generation.");
            return;
        }

        System.out.println("Generating test data for Dashboard & Race Details...");

        // 1. Create Admin
        Admin admin = new Admin();
        admin.setUsername("admin1");
        admin.setPassword("password123");
        admin.setEmail("admin1@test.com");
        admin.setRole(UserRole.ADMIN.name());
        adminRepository.save(admin);

        // 2. Create Tournaments for Dashboard (Various Statuses)
        Tournament t1 = new Tournament();
        t1.setAdmin(admin);
        t1.setName("Spring Championship");
        t1.setStartDate(LocalDate.now().plusDays(5));
        t1.setEndDate(LocalDate.now().plusDays(10));
        t1.setAllowedBreed("Thoroughbred");
        t1.setAllowedHorseAge(4);
        t1.setStatus("PUBLIC");
        tournamentRepository.save(t1);

        Tournament t2 = new Tournament();
        t2.setAdmin(admin);
        t2.setName("Summer Cup");
        t2.setStartDate(LocalDate.now().minusDays(5));
        t2.setEndDate(LocalDate.now().plusDays(5));
        t2.setAllowedBreed("Arabian");
        t2.setAllowedHorseAge(5);
        t2.setStatus("ONGOING");
        tournamentRepository.save(t2);

        Tournament t3 = new Tournament();
        t3.setAdmin(admin);
        t3.setName("Winter Classics");
        t3.setStartDate(LocalDate.now().minusDays(20));
        t3.setEndDate(LocalDate.now().minusDays(15));
        t3.setAllowedBreed("Any");
        t3.setAllowedHorseAge(6);
        t3.setStatus("FINISHED");
        tournamentRepository.save(t3);

        // 3. Create Races for Tournament 1 (To test Race Details API)
        Race r1 = new Race();
        r1.setTournament(t1);
        r1.setName("Qualifier 1");
        r1.setDate(LocalDate.now().plusDays(6));
        r1.setStartTime(LocalTime.of(8, 0));
        r1.setEndTime(LocalTime.of(9, 0));
        r1.setLaps(5);
        r1.setNumHorse(10);
        r1.setStatus("SCHEDULED");
        raceRepository.save(r1);

        Race r2 = new Race();
        r2.setTournament(t1);
        r2.setName("Qualifier 2");
        r2.setDate(LocalDate.now().plusDays(6));
        r2.setStartTime(LocalTime.of(10, 0));
        r2.setEndTime(LocalTime.of(11, 0));
        r2.setLaps(5);
        r2.setNumHorse(8);
        r2.setStatus("SCHEDULED");
        raceRepository.save(r2);

        Race r3 = new Race();
        r3.setTournament(t1);
        r3.setName("Finals");
        r3.setDate(LocalDate.now().plusDays(9));
        r3.setStartTime(LocalTime.of(15, 0));
        r3.setEndTime(LocalTime.of(16, 0));
        r3.setLaps(10);
        r3.setNumHorse(12);
        r3.setStatus("SCHEDULED");
        raceRepository.save(r3);

        // 4. Create Jockey and Verification Requests
        com.swp.hrtms.hrtmsbe.entity.Jockey jockey = new com.swp.hrtms.hrtmsbe.entity.Jockey();
        jockey.setUsername("jockey1");
        jockey.setPassword("pass123");
        jockey.setEmail("jockey1@test.com");
        jockey.setRole("JOCKEY");
        jockey.setJockeyName("John Doe");
        jockey.setYearOfExperience(5);
        jockey.setAge(28);
        jockey.setStatus(true);
        jockey = (com.swp.hrtms.hrtmsbe.entity.Jockey) userRepository.save(jockey); // save as user

        com.swp.hrtms.hrtmsbe.entity.JockeyCert cert1 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
        cert1.setCertName("Health Certificate 2024");
        cert1.setStatus("PENDING");
        cert1.setJockey(jockey);
        cert1.setCertImg("mock-image-data-1".getBytes());
        jockeyCertRepository.save(cert1);

        com.swp.hrtms.hrtmsbe.entity.JockeyCert cert2 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
        cert2.setCertName("Pro License Level B");
        cert2.setStatus("PENDING");
        cert2.setJockey(jockey);
        cert2.setCertImg("mock-image-data-2".getBytes());
        jockeyCertRepository.save(cert2);

        com.swp.hrtms.hrtmsbe.entity.Notification notification = new com.swp.hrtms.hrtmsbe.entity.Notification();
        notification.setSender(jockey);
        notification.setTitle("New Certificate Verification Request");
        notification.setContent("Jockey John Doe has submitted new certificates for verification.");
        notification.setType("VERIFY_CERTIFICATE");
        notification = notificationRepository.save(notification);

        com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
        recipient.setNotification(notification);
        recipient.setRecipient(admin);
        recipient.setStatus("None");
        notificationRecipientRepository.save(recipient);

        System.out.println("Test data generated successfully!");
    }
}
