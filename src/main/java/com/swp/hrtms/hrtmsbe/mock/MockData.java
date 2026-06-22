package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Horse;
import com.swp.hrtms.hrtmsbe.entity.HorseOwner;
import com.swp.hrtms.hrtmsbe.entity.HorseStatus;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Spectator;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.entity.UserRole;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class MockData {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final HorseOwnerRepository horseOwnerRepository;
    private final HorseRepository horseRepository;
    private final TournamentRepository tournamentRepository;
    private final RaceRepository raceRepository;
    private final RefereeRepository refereeRepository;
    private final com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository jockeyCertRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository;

    public MockData(UserRepository userRepository,
            AdminRepository adminRepository,
            HorseOwnerRepository horseOwnerRepository,
            HorseRepository horseRepository,
            TournamentRepository tournamentRepository,
            RaceRepository raceRepository,
            RefereeRepository refereeRepository,
            com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository jockeyCertRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.horseOwnerRepository = horseOwnerRepository;
        this.horseRepository = horseRepository;
        this.tournamentRepository = tournamentRepository;
        this.raceRepository = raceRepository;
        this.refereeRepository = refereeRepository;
        this.jockeyCertRepository = jockeyCertRepository;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    public void generateData() {
        boolean dataAlreadyExists = userRepository.count() > 0;

        generateHorseOwnerProfileData();
        generateSpectatorProfileData();
        generateHorseData();

        // Check if data already exists to avoid duplicate data on application restart
        if (dataAlreadyExists) {
            generateHorseOwnerNotificationData();
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
        t1.setStatus("DRAFT");
        tournamentRepository.save(t1);

        Tournament t2 = new Tournament();
        t2.setAdmin(admin);
        t2.setName("Summer Cup");
        t2.setStartDate(LocalDate.now().minusDays(5));
        t2.setEndDate(LocalDate.now().plusDays(5));
        t2.setAllowedBreed("Arabian");
        t2.setAllowedHorseAge(5);
        t2.setStatus("PUBLISHED");
        tournamentRepository.save(t2);

        Tournament t3 = new Tournament();
        t3.setAdmin(admin);
        t3.setName("Winter Classics");
        t3.setStartDate(LocalDate.now().minusDays(20));
        t3.setEndDate(LocalDate.now().minusDays(15));
        t3.setAllowedBreed("Any");
        t3.setAllowedHorseAge(6);
        t3.setStatus("COMPLET");
        tournamentRepository.save(t3);

        Tournament t4 = new Tournament();
        t4.setAdmin(admin);
        t4.setName("Autumn Sprint");
        t4.setStartDate(LocalDate.now().minusDays(10));
        t4.setEndDate(LocalDate.now().minusDays(5));
        t4.setAllowedBreed("Quarter Horse");
        t4.setAllowedHorseAge(3);
        t4.setStatus("CANCELLED");
        tournamentRepository.save(t4);

        // Create Referees
        Referee ref1 = new Referee();
        ref1.setUsername("referee1");
        ref1.setPassword("password123");
        ref1.setEmail("ref1@test.com");
        ref1.setRole(UserRole.REFEREE.name());
        ref1.setName("John Referee");
        refereeRepository.save(ref1);

        Referee ref2 = new Referee();
        ref2.setUsername("referee2");
        ref2.setPassword("password123");
        ref2.setEmail("ref2@test.com");
        ref2.setRole(UserRole.REFEREE.name());
        ref2.setName("Mike Referee");
        refereeRepository.save(ref2);

        // 3. Create Races for Tournament 1 (To test Race Details API)
        Race r1 = new Race();
        r1.setTournament(t1);
        r1.setName("Qualifier 1");
        r1.setDate(LocalDate.now().plusDays(6));
        r1.setStartTime(LocalTime.of(8, 0));
        r1.setEndTime(LocalTime.of(9, 0));
        r1.setLaps(5);
        r1.setNumHorse(10);
        r1.setStatus("PENDING_REFEREE");
        r1.setReferee(ref1);
        raceRepository.save(r1);

        Race r2 = new Race();
        r2.setTournament(t1);
        r2.setName("Qualifier 2");
        r2.setDate(LocalDate.now().plusDays(6));
        r2.setStartTime(LocalTime.of(10, 0));
        r2.setEndTime(LocalTime.of(11, 0));
        r2.setLaps(5);
        r2.setNumHorse(8);
        r2.setStatus("PUBLISHED");
        r2.setReferee(ref2);
        raceRepository.save(r2);

        Race r3 = new Race();
        r3.setTournament(t1);
        r3.setName("Finals");
        r3.setDate(LocalDate.now().plusDays(9));
        r3.setStartTime(LocalTime.of(15, 0));
        r3.setEndTime(LocalTime.of(16, 0));
        r3.setLaps(10);
        r3.setNumHorse(12);
        r3.setStatus("CANCELLD");
        r3.setReferee(ref1);
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
        cert1.setCertImg("mock-image-data-1");
        jockeyCertRepository.save(cert1);

        com.swp.hrtms.hrtmsbe.entity.JockeyCert cert2 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
        cert2.setCertName("Pro License Level B");
        cert2.setStatus("PENDING");
        cert2.setJockey(jockey);
        cert2.setCertImg("mock-image-data-2");
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

        // Historical certificate results delivered to the jockey. These records
        // support GET /api/v1/notifications/jockeys/{jockeyId}/certificate-results.
        createCertificateResultNotification(
                admin,
                jockey,
                "Certificate Verification Rejected",
                "The certificate image is unclear. Please upload a clearer image.",
                "REJECT_CERTIFICATE",
                LocalDateTime.now().minusDays(2));

        createCertificateResultNotification(
                admin,
                jockey,
                "Certificate Verified",
                "Your certificates have been verified successfully.",
                "ACCEPT_CERTIFICATE",
                LocalDateTime.now().minusDays(1));

        generateHorseOwnerNotificationData();

        System.out.println("Test data generated successfully!");
    }

    private void createCertificateResultNotification(
            User admin,
            User jockey,
            String title,
            String content,
            String type,
            LocalDateTime createdAt) {
        com.swp.hrtms.hrtmsbe.entity.Notification notification =
                new com.swp.hrtms.hrtmsbe.entity.Notification();
        notification.setSender(admin);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setCreatedAt(createdAt);
        notification = notificationRepository.save(notification);

        com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient =
                new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
        recipient.setNotification(notification);
        recipient.setRecipient(jockey);
        recipient.setStatus("None");
        notificationRecipientRepository.save(recipient);
    }

    private void generateHorseOwnerProfileData() {
        User horseOwnerUser = userRepository.findByUsername("horseowner1")
                .orElseGet(() -> {
                    User user = new User();
                    user.setUsername("horseowner1");
                    user.setPassword("password123");
                    user.setEmail("horseowner1@test.com");
                    user.setRole(UserRole.HORSE_OWNER.name());
                    return userRepository.save(user);
                });

        HorseOwner horseOwner = horseOwnerRepository.findById(horseOwnerUser.getId())
                .orElseGet(() -> horseOwnerRepository.save(HorseOwner.builder()
                        .user(horseOwnerUser)
                        .build()));

        System.out.println("Horse owner profile test URL: /api/horse-owners/"
                + horseOwner.getUserId() + "/profile");
    }

    private void generateSpectatorProfileData() {
        if (userRepository.findByUsername("spectator1").isPresent()) {
            return;
        }

        Spectator spectator = new Spectator();
        spectator.setUsername("spectator1");
        spectator.setPassword("password123");
        spectator.setEmail("spectator1@test.com");
        spectator.setRole(UserRole.SPECTATOR.name());
        spectator.setDisplayName("Test Spectator");
        userRepository.save(spectator);
    }

    private void generateHorseData() {
        if (horseRepository.count() > 0) {
            return;
        }

        User ownerUser = userRepository.findByUsername("horseowner1")
                .orElseThrow(() -> new IllegalStateException("Mock horse owner was not created"));
        HorseOwner owner = horseOwnerRepository.findById(ownerUser.getId())
                .orElseThrow(() -> new IllegalStateException("Mock horse owner profile was not created"));

        List<Horse> horses = List.of(
                Horse.builder()
                        .owner(owner)
                        .name("Thunder Bolt")
                        .age(4)
                        .breed("Thoroughbred")
                        .status(HorseStatus.ACTIVE)
                        .build(),
                Horse.builder()
                        .owner(owner)
                        .name("Silver Wind")
                        .age(5)
                        .breed("Arabian")
                        .status(HorseStatus.ACTIVE)
                        .build(),
                Horse.builder()
                        .owner(owner)
                        .name("Black Pearl")
                        .age(3)
                        .breed("Thoroughbred")
                        .status(HorseStatus.ACTIVE)
                        .build(),
                Horse.builder()
                        .owner(owner)
                        .name("Golden Star")
                        .age(6)
                        .breed("Quarter Horse")
                        .status(HorseStatus.INJURED)
                        .build(),
                Horse.builder()
                        .owner(owner)
                        .name("Old Champion")
                        .age(10)
                        .breed("Arabian")
                        .status(HorseStatus.RETIRED)
                        .build());

        horseRepository.saveAll(horses);
    }

    private void generateHorseOwnerNotificationData() {
        User horseOwner = userRepository.findByUsername("horseowner1").orElse(null);
        User admin = userRepository.findByUsername("admin1").orElse(null);
        User jockey = userRepository.findByUsername("jockey1").orElse(null);

        if (horseOwner == null || admin == null || jockey == null) {
            System.out.println("Skipping horse owner notifications: mock owner, admin, or jockey is missing.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<HorseOwnerNotificationSeed> seeds = List.of(
                new HorseOwnerNotificationSeed(admin, "New tournament published",
                        "Spring Championship is now open for horse registration.",
                        "NEW_TOURNAMENT", now.minusDays(10), now.minusDays(9)),
                new HorseOwnerNotificationSeed(admin, "Tournament schedule updated",
                        "The schedule for Spring Championship has been updated.",
                        "TOURNAMENT_UPDATE", now.minusDays(9), null),
                new HorseOwnerNotificationSeed(admin, "Tournament cancelled",
                        "Autumn Sprint has been cancelled by the organizer.",
                        "TOURNAMENT_CANCELLED", now.minusDays(8), now.minusDays(7)),
                new HorseOwnerNotificationSeed(admin, "New race announced",
                        "Qualifier 1 has been added to Spring Championship.",
                        "NEW_RACE", now.minusDays(7), null),
                new HorseOwnerNotificationSeed(admin, "Race schedule updated",
                        "Qualifier 2 will start at 10:00 AM.",
                        "RACE_UPDATE", now.minusDays(6), now.minusDays(5)),
                new HorseOwnerNotificationSeed(admin, "Race cancelled",
                        "The Finals race has been cancelled.",
                        "RACE_CANCELLED", now.minusDays(5), null),
                new HorseOwnerNotificationSeed(admin, "Registration approved",
                        "Thunder Bolt has been approved for Qualifier 1.",
                        "REGISTRATION_APPROVED", now.minusDays(4), now.minusDays(3)),
                new HorseOwnerNotificationSeed(admin, "Registration rejected",
                        "Golden Star was rejected because the horse is currently injured.",
                        "REGISTRATION_REJECTED", now.minusDays(3), null),
                new HorseOwnerNotificationSeed(jockey, "Jockey invitation accepted",
                        "John Doe accepted your invitation to ride Thunder Bolt.",
                        "INVITATION_ACCEPTED", now.minusDays(2), now.minusDays(1)),
                new HorseOwnerNotificationSeed(jockey, "Jockey invitation rejected",
                        "John Doe rejected your invitation to ride Silver Wind.",
                        "INVITATION_REJECTED", now.minusDays(1), null));

        List<String> existingTitles = notificationRecipientRepository.findHorseOwnerNotifications(
                        horseOwner.getId(),
                        List.of(
                                "NEW_TOURNAMENT", "TOURNAMENT_UPDATE", "TOURNAMENT_CANCELLED",
                                "NEW_RACE", "RACE_UPDATE", "RACE_CANCELLED",
                                "REGISTRATION_APPROVED", "REGISTRATION_REJECTED"),
                        List.of("INVITATION_ACCEPTED", "INVITATION_REJECTED"))
                .stream()
                .map(recipient -> recipient.getNotification().getTitle())
                .toList();

        for (HorseOwnerNotificationSeed seed : seeds) {
            if (existingTitles.contains(seed.title())) {
                continue;
            }

            Notification notification = Notification.builder()
                    .sender(seed.sender())
                    .title(seed.title())
                    .content(seed.content())
                    .type(seed.type())
                    .createdAt(seed.createdAt())
                    .build();
            notification = notificationRepository.save(notification);

            notificationRecipientRepository.save(NotificationRecipient.builder()
                    .notification(notification)
                    .recipient(horseOwner)
                    .status("None")
                    .readAt(seed.readAt())
                    .build());
        }

        System.out.println("Horse owner notification test URL: /api/v1/notifications/horse-owners/"
                + horseOwner.getId());
    }

    private record HorseOwnerNotificationSeed(
            User sender,
            String title,
            String content,
            String type,
            LocalDateTime createdAt,
            LocalDateTime readAt) {
    }
}
