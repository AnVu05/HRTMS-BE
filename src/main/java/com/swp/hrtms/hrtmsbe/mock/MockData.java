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
                generateHorseData();

                // Check if data already exists to avoid duplicate data on application restart
                if (dataAlreadyExists) {
                        System.out.println("Data already exists. Skipping test data generation.");
                        return;
                }

                System.out.println("Generating INTERNATIONAL REALISTIC test data for HRTMS APIs...");

                // ==========================================
                // 1. Create Admin
                // ==========================================
                Admin admin = new Admin();
                admin.setUsername("admin_global");
                admin.setPassword("password123");
                admin.setEmail("admin.global@hrtms.com");
                admin.setRole(UserRole.ADMIN.name());
                adminRepository.save(admin);

                // ==========================================
                // 2. Create Referees (Stewards) - For GET /api/v1/referees
                // ==========================================
                Referee ref1 = new Referee();
                ref1.setUsername("referee_prosser");
                ref1.setPassword("password123");
                ref1.setEmail("m.prosser@racing.com");
                ref1.setRole(UserRole.REFEREE.name());
                ref1.setName("Michael Prosser (Chief Steward)");
                refereeRepository.save(ref1);

                Referee ref2 = new Referee();
                ref2.setUsername("referee_kelly");
                ref2.setPassword("password123");
                ref2.setEmail("k.kelly@racing.com");
                ref2.setRole(UserRole.REFEREE.name());
                ref2.setName("Kim Kelly");
                refereeRepository.save(ref2);

                // ==========================================
                // 3. Create Tournaments - For Dashboard, PUT /id, PUT /cancel, POST
                // ==========================================
                Tournament t1 = new Tournament();
                t1.setAdmin(admin);
                t1.setName("Kentucky Derby 2026");
                t1.setStartDate(LocalDate.now().plusDays(10));
                t1.setEndDate(LocalDate.now().plusDays(12));
                t1.setAllowedBreed("Thoroughbred");
                t1.setAllowedHorseAge(3);
                t1.setStatus("PUBLISHED");
                t1 = tournamentRepository.save(t1);

                Tournament t2 = new Tournament();
                t2.setAdmin(admin);
                t2.setName("Dubai World Cup 2026");
                t2.setStartDate(LocalDate.now().minusDays(15));
                t2.setEndDate(LocalDate.now().minusDays(14));
                t2.setAllowedBreed("Thoroughbred & Arabian");
                t2.setAllowedHorseAge(4);
                t2.setStatus("COMPLETED");
                t2 = tournamentRepository.save(t2);

                Tournament t3 = new Tournament();
                t3.setAdmin(admin);
                t3.setName("Prix de l'Arc de Triomphe 2026");
                t3.setStartDate(LocalDate.now().plusDays(30));
                t3.setEndDate(LocalDate.now().plusDays(32));
                t3.setAllowedBreed("Thoroughbred");
                t3.setAllowedHorseAge(3);
                t3.setStatus("DRAFT");
                tournamentRepository.save(t3);

                Tournament t4 = new Tournament();
                t4.setAdmin(admin);
                t4.setName("Melbourne Cup 2026");
                t4.setStartDate(LocalDate.now().plusDays(40));
                t4.setEndDate(LocalDate.now().plusDays(45));
                t4.setAllowedBreed("Any");
                t4.setAllowedHorseAge(5);
                t4.setStatus("CANCELLED");
                tournamentRepository.save(t4);

                // ==========================================
                // 4. Create Independent Races (All belong to Kentucky Derby but are completely
                // independent)
                // ==========================================
                Race r1 = new Race();
                r1.setTournament(t1);
                r1.setName("The Churchill Downs Stakes (G1) - 1400m Dirt");
                r1.setDate(LocalDate.now().plusDays(11));
                r1.setStartTime(LocalTime.of(13, 0));
                r1.setEndTime(LocalTime.of(13, 15));
                r1.setLaps(1);
                r1.setNumHorse(14);
                r1.setStatus("PENDING_REFEREE");
                r1.setReferee(ref1);
                raceRepository.save(r1);

                Race r2 = new Race();
                r2.setTournament(t1);
                r2.setName("The Turf Classic (G1) - 1800m Turf");
                r2.setDate(LocalDate.now().plusDays(11));
                r2.setStartTime(LocalTime.of(14, 30));
                r2.setEndTime(LocalTime.of(14, 45));
                r2.setLaps(1);
                r2.setNumHorse(12);
                r2.setStatus("PUBLISHED");
                r2.setReferee(ref2);
                raceRepository.save(r2);

                Race r3 = new Race();
                r3.setTournament(t1);
                r3.setName("The Derby City Distaff (G1) - 1600m Dirt");
                r3.setDate(LocalDate.now().plusDays(12));
                r3.setStartTime(LocalTime.of(15, 0));
                r3.setEndTime(LocalTime.of(15, 15));
                r3.setLaps(1);
                r3.setNumHorse(10);
                r3.setStatus("CANCELLED");
                r3.setReferee(ref1);
                raceRepository.save(r3);

                // ==========================================
                // 5. Create Jockey & Jockey Certs - For Verifications API
                // ==========================================
                com.swp.hrtms.hrtmsbe.entity.Jockey jockey1 = new com.swp.hrtms.hrtmsbe.entity.Jockey();
                jockey1.setUsername("jockey_dettori");
                jockey1.setPassword("pass123");
                jockey1.setEmail("f.dettori@jockey.com");
                jockey1.setRole("JOCKEY");
                jockey1.setJockeyName("Frankie Dettori");
                jockey1.setYearOfExperience(30);
                jockey1.setAge(52);
                jockey1.setStatus(false); // Pending verification
                jockey1 = (com.swp.hrtms.hrtmsbe.entity.Jockey) userRepository.save(jockey1);

                com.swp.hrtms.hrtmsbe.entity.Jockey jockey2 = new com.swp.hrtms.hrtmsbe.entity.Jockey();
                jockey2.setUsername("jockey_moore");
                jockey2.setPassword("pass123");
                jockey2.setEmail("r.moore@jockey.com");
                jockey2.setRole("JOCKEY");
                jockey2.setJockeyName("Ryan Moore");
                jockey2.setYearOfExperience(20);
                jockey2.setAge(40);
                jockey2.setStatus(false); // Pending verification
                jockey2 = (com.swp.hrtms.hrtmsbe.entity.Jockey) userRepository.save(jockey2);

                // Create Horse Owner User
                User userOwner = new User();
                userOwner.setUsername("owner_godolphin");
                userOwner.setEmail("contact@godolphin.com");
                userOwner.setPassword("pass123");
                userOwner.setRole("HORSE_OWNER");
                userOwner = userRepository.save(userOwner);

                com.swp.hrtms.hrtmsbe.entity.HorseOwner owner = new com.swp.hrtms.hrtmsbe.entity.HorseOwner();
                owner.setUser(userOwner);
                horseOwnerRepository.save(owner);

                // Create Spectator
                com.swp.hrtms.hrtmsbe.entity.Spectator spectator = new com.swp.hrtms.hrtmsbe.entity.Spectator();
                spectator.setUsername("spectator_vip");
                spectator.setPassword("pass123");
                spectator.setEmail("vip.member@racingfans.com");
                spectator.setRole("SPECTATOR");
                userRepository.save(spectator);

                // Create Certificates for Verifications Test
                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert1 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
                cert1.setCertName("International Medical Clearance 2026");
                cert1.setStatus("PENDING");
                cert1.setJockey(jockey1);
                cert1.setCertImg(
                                "/9j/4AAQSkZJRgABAQAAAQABAAD/4gIoSUNDX1BST0ZJTEUAAQEAAAIYanhsIARAAAB");
                jockeyCertRepository.save(cert1);

                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert2 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
                cert2.setCertName("International Medical Clearance 2026");
                cert2.setStatus("PENDING");
                cert2.setJockey(jockey1);
                cert2.setCertImg("International Medical Clearance 2026");
                jockeyCertRepository.save(cert2);

                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert3 = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
                cert3.setCertName("Jockey Club of North America Riding Permit");
                cert3.setStatus("PENDING");
                cert3.setJockey(jockey2);
                jockeyCertRepository.save(cert3);
                cert3.setCertImg(
                                "iVBORw0KGgoAAAANSUhEUgAAA8YAAADFCAYAAACW7evyAAAAAXNSR0IArs4c6QAAAAR");
                jockeyCertRepository.save(cert3);

                // Notifications
                com.swp.hrtms.hrtmsbe.entity.Notification notif1 = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notif1.setSender(jockey1);
                notif1.setTitle("Jockey Verification Request");
                notif1.setContent(
                                "Jockey Frankie Dettori has uploaded new certification documents. Please review and verify.");
                notif1.setType("VERIFY_CERTIFICATE");
                notif1 = notificationRepository.save(notif1);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec1 = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                rec1.setNotification(notif1);
                rec1.setRecipient(admin);
                rec1.setStatus("UNREAD");
                notificationRecipientRepository.save(rec1);

                com.swp.hrtms.hrtmsbe.entity.Notification notif2 = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notif2.setSender(jockey2);
                notif2.setTitle("Jockey Verification Request");
                notif2.setContent(
                                "Jockey Ryan Moore has uploaded new certification documents. Please review and verify.");
                notif2.setType("VERIFY_CERTIFICATE");
                notif2 = notificationRepository.save(notif2);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec2 = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                rec2.setNotification(notif2);
                rec2.setRecipient(admin);
                rec2.setStatus("UNREAD");
                notificationRecipientRepository.save(rec2);

                com.swp.hrtms.hrtmsbe.entity.Notification notif3 = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notif3.setTitle("Referee Accepted");
                notif3.setContent(
                                "Referee Michael Prosser has accepted the assignment for race The Churchill Downs Stakes.");
                notif3.setType("REFEREE_ACCEPTED");
                notif3 = notificationRepository.save(notif3);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec3 = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                rec3.setNotification(notif3);
                rec3.setRecipient(admin);
                rec3.setStatus("UNREAD");
                notificationRecipientRepository.save(rec3);

                com.swp.hrtms.hrtmsbe.entity.Notification notif4 = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notif4.setTitle("Registration Verify");
                notif4.setContent("A new horse registration requires your verification.");
                notif4.setType("REGISTRATION_VERIFY");
                notif4 = notificationRepository.save(notif4);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec4 = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                rec4.setNotification(notif4);
                rec4.setRecipient(admin);
                rec4.setStatus("UNREAD");
                notificationRecipientRepository.save(rec4);

                com.swp.hrtms.hrtmsbe.entity.Notification notif5 = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notif5.setTitle("Doctor Rejected");
                notif5.setContent("Doctor has rejected the horse medical clearance for Flightline.");
                notif5.setType("DOCTOR_REJECTED");
                notif5 = notificationRepository.save(notif5);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec5 = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                rec5.setNotification(notif5);
                rec5.setRecipient(admin);
                rec5.setStatus("UNREAD");
                notificationRecipientRepository.save(rec5);

                com.swp.hrtms.hrtmsbe.entity.Notification notif6 = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notif6.setTitle("Doctor Accepted");
                notif6.setContent("Doctor has accepted the horse medical clearance for Baaeed.");
                notif6.setType("DOCTOR_ACCEPTED");
                notif6 = notificationRepository.save(notif6);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec6 = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                rec6.setNotification(notif6);
                rec6.setRecipient(admin);
                rec6.setStatus("READ");
                rec6.setReadAt(LocalDateTime.now().minusDays(1));
                notificationRecipientRepository.save(rec6);

                com.swp.hrtms.hrtmsbe.entity.Notification notif7 = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notif7.setTitle("Referee Rejected");
                notif7.setContent("Referee Kim Kelly has rejected the assignment for race The Turf Classic.");
                notif7.setType("REFEREE_REJECTED");
                notif7 = notificationRepository.save(notif7);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec7 = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                rec7.setNotification(notif7);
                rec7.setRecipient(admin);
                rec7.setStatus("READ");
                rec7.setReadAt(LocalDateTime.now().minusHours(5));
                notificationRecipientRepository.save(rec7);

                System.out.println("Test data generated successfully!");
        }

        private void createCertificateResultNotification(
                        User admin,
                        User jockey,
                        String title,
                        String content,
                        String type,
                        LocalDateTime createdAt) {
                com.swp.hrtms.hrtmsbe.entity.Notification notification = new com.swp.hrtms.hrtmsbe.entity.Notification();
                notification.setSender(admin);
                notification.setTitle(title);
                notification.setContent(content);
                notification.setType(type);
                notification.setCreatedAt(createdAt);
                notification = notificationRepository.save(notification);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = new com.swp.hrtms.hrtmsbe.entity.NotificationRecipient();
                recipient.setNotification(notification);
                recipient.setRecipient(jockey);
                recipient.setStatus("None");
                notificationRecipientRepository.save(recipient);
        }

        private void generateHorseOwnerProfileData() {
                User horseOwnerUser = userRepository.findByUsername("owner_coolmore")
                                .orElseGet(() -> {
                                        User user = new User();
                                        user.setUsername("owner_coolmore");
                                        user.setPassword("password123");
                                        user.setEmail("contact@coolmore.com");
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

        private void generateHorseData() {
                if (horseRepository.count() > 0) {
                        return;
                }

                User ownerUser = userRepository.findByUsername("owner_coolmore")
                                .orElseThrow(() -> new IllegalStateException("Mock horse owner was not created"));
                HorseOwner owner = horseOwnerRepository.findById(ownerUser.getId())
                                .orElseThrow(() -> new IllegalStateException(
                                                "Mock horse owner profile was not created"));

                List<Horse> horses = List.of(
                                Horse.builder()
                                                .owner(owner)
                                                .name("Flightline")
                                                .age(4)
                                                .breed("Thoroughbred")
                                                .status(HorseStatus.ACTIVE)
                                                .build(),
                                Horse.builder()
                                                .owner(owner)
                                                .name("Baaeed")
                                                .age(4)
                                                .breed("Thoroughbred")
                                                .status(HorseStatus.ACTIVE)
                                                .build(),
                                Horse.builder()
                                                .owner(owner)
                                                .name("Equinox")
                                                .age(3)
                                                .breed("Thoroughbred")
                                                .status(HorseStatus.ACTIVE)
                                                .build(),
                                Horse.builder()
                                                .owner(owner)
                                                .name("City Of Troy")
                                                .age(2)
                                                .breed("Thoroughbred")
                                                .status(HorseStatus.INJURED)
                                                .build(),
                                Horse.builder()
                                                .owner(owner)
                                                .name("Frankel")
                                                .age(14)
                                                .breed("Thoroughbred")
                                                .status(HorseStatus.RETIRED)
                                                .build());

                horseRepository.saveAll(horses);
        }
}
