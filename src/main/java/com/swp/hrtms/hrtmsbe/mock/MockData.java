package com.swp.hrtms.hrtmsbe.mock;

import com.swp.hrtms.hrtmsbe.entity.Admin;
import com.swp.hrtms.hrtmsbe.entity.Notification;
import com.swp.hrtms.hrtmsbe.entity.NotificationRecipient;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.entity.Tournament;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.entity.UserRole;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.repository.JockeyCertRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository;
import com.swp.hrtms.hrtmsbe.repository.NotificationRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class MockData {

        private final UserRepository userRepository;
        private final AdminRepository adminRepository;
        private final TournamentRepository tournamentRepository;
        private final RaceRepository raceRepository;
        private final RefereeRepository refereeRepository;
        private final JockeyCertRepository jockeyCertRepository;
        private final NotificationRepository notificationRepository;
        private final NotificationRecipientRepository notificationRecipientRepository;

        public MockData(
                        UserRepository userRepository,
                        AdminRepository adminRepository,
                        TournamentRepository tournamentRepository,
                        RaceRepository raceRepository,
                        RefereeRepository refereeRepository,
                        JockeyCertRepository jockeyCertRepository,
                        NotificationRepository notificationRepository,
                        NotificationRecipientRepository notificationRecipientRepository) {
                this.userRepository = userRepository;
                this.adminRepository = adminRepository;
                this.tournamentRepository = tournamentRepository;
                this.raceRepository = raceRepository;
                this.refereeRepository = refereeRepository;
                this.jockeyCertRepository = jockeyCertRepository;
                this.notificationRepository = notificationRepository;
                this.notificationRecipientRepository = notificationRecipientRepository;
        }

        public void generateData() {
                generateJockeyCertificateAndRaceFlowMockDataOnStartup();
        }

        public void generateJockeyCertificateAndRaceFlowMockDataOnStartup() {
                System.out.println("Seeding mock data for jockey certificate flow and race creation flow...");

                Admin admin = findOrCreateAdmin();
                Referee refereeGregCarpenter = findOrCreateReferee("referee1", "ref1@test.com", "Greg Carpenter");
                Referee refereeMichaelWrona = findOrCreateReferee("referee2", "ref2@test.com", "Michael Wrona");
                com.swp.hrtms.hrtmsbe.entity.Jockey jockeyRyanMoore = findOrCreateJockey(
                                "jockey1",
                                "jockey1@test.com",
                                "Ryan Moore",
                                22,
                                42,
                                "Royal Ascot and international Group 1 winning jockey.");

                Tournament royalAscot = findOrCreateTournament(admin);

                Race queenAnneStakes = findOrCreateRace(
                                royalAscot,
                                refereeGregCarpenter,
                                "Queen Anne Stakes",
                                java.time.LocalDate.of(2026, 6, 23),
                                java.time.LocalTime.of(9, 0),
                                java.time.LocalTime.of(10, 0),
                                "PENDING_REFEREE");

                Race ascotGoldCup = findOrCreateRace(
                                royalAscot,
                                refereeMichaelWrona,
                                "Ascot Gold Cup",
                                java.time.LocalDate.of(2026, 6, 24),
                                java.time.LocalTime.of(14, 0),
                                java.time.LocalTime.of(15, 0),
                                "PUBLISHED");

                findOrCreateJockeyCertificate(
                                jockeyRyanMoore,
                                "Professional Racing License",
                                "sdfsd",
                                "PENDING");

                findOrCreateJockeyCertificate(
                                jockeyRyanMoore,
                                "Safety Certificate",
                                "sdfsd",
                                "PENDING");

                findOrCreateJockeyCertificate(
                                jockeyRyanMoore,
                                "Verified Racing Stewardship Training",
"sdfsd",                                "VERIFIED");

                findOrCreateNotificationRecipient(
                                jockeyRyanMoore,
                                admin,
                                null,
                                "Certificate Verification Request",
                                "Jockey Ryan Moore has submitted certificates for verification.",
                                "VERIFY_CERTIFICATE",
                                "None",
                                null);

                findOrCreateNotificationRecipient(
                                admin,
                                jockeyRyanMoore,
                                null,
                                "Certificate Verified",
                                "Your certificates have been verified successfully.",
                                "ACCEPT_CERTIFICATE",
                                "READ",
                                java.time.LocalDateTime.now().minusDays(2));

                findOrCreateNotificationRecipient(
                                admin,
                                jockeyRyanMoore,
                                null,
                                "Certificate Verification Rejected",
                                "Certificate image is unclear.",
                                "REJECT_CERTIFICATE",
                                "None",
                                java.time.LocalDateTime.now().minusDays(1));

                findOrCreateNotificationRecipient(
                                admin,
                                refereeGregCarpenter,
                                queenAnneStakes,
                                "Referee Invitation",
                                "You are invited to referee race Queen Anne Stakes.",
                                "REFEREE_INVITATION",
                                "None",
                                null);

                findOrCreateNotificationRecipient(
                                admin,
                                refereeMichaelWrona,
                                ascotGoldCup,
                                "Referee Invitation Accepted",
                                "You accepted the invitation to referee race Ascot Gold Cup.",
                                "REFEREE_INVITATION",
                                "READ",
                                java.time.LocalDateTime.now().minusHours(4));

                System.out.println("Seeded mock data successfully.");
                System.out.println("Admin: admin1 / password123, id=" + admin.getId());
                System.out.println("Jockey: jockey1 / password123, id=" + jockeyRyanMoore.getId());
                System.out.println("Referee 1: referee1 / password123, id=" + refereeGregCarpenter.getId());
                System.out.println("Referee 2: referee2 / password123, id=" + refereeMichaelWrona.getId());
                System.out.println("Tournament Royal Ascot id=" + royalAscot.getId());
                System.out.println("Pending race Queen Anne Stakes id=" + queenAnneStakes.getId());
                System.out.println("Published race Ascot Gold Cup id=" + ascotGoldCup.getId());
        }

        private Admin findOrCreateAdmin() {
                java.util.Optional<User> existingAdminUser = userRepository.findByUsername("admin1");
                if (existingAdminUser.isPresent()) {
                        return (Admin) existingAdminUser.get();
                }

                Admin admin = new Admin();
                admin.setUsername("admin1");
                admin.setPassword("password123");
                admin.setEmail("admin1@test.com");
                admin.setRole(UserRole.ADMIN.name());
                return adminRepository.save(admin);
        }

        private Referee findOrCreateReferee(String username, String email, String name) {
                java.util.Optional<User> existingRefereeUser = userRepository.findByUsername(username);
                if (existingRefereeUser.isPresent()) {
                        Referee referee = (Referee) existingRefereeUser.get();
                        referee.setName(name);
                        return refereeRepository.save(referee);
                }

                Referee referee = new Referee();
                referee.setUsername(username);
                referee.setPassword("password123");
                referee.setEmail(email);
                referee.setRole(UserRole.REFEREE.name());
                referee.setName(name);
                return refereeRepository.save(referee);
        }

        private com.swp.hrtms.hrtmsbe.entity.Jockey findOrCreateJockey(
                        String username,
                        String email,
                        String jockeyName,
                        Integer yearsOfExperience,
                        Integer age,
                        String professionalBio) {
                java.util.Optional<User> existingJockeyUser = userRepository.findByUsername(username);
                if (existingJockeyUser.isPresent()) {
                        com.swp.hrtms.hrtmsbe.entity.Jockey jockey = (com.swp.hrtms.hrtmsbe.entity.Jockey) existingJockeyUser
                                        .get();
                        jockey.setJockeyName(jockeyName);
                        jockey.setYearOfExperience(yearsOfExperience);
                        jockey.setAge(age);
                        jockey.setProfessionalBio(professionalBio);
                        jockey.setStatus(true);
                        return (com.swp.hrtms.hrtmsbe.entity.Jockey) userRepository.save(jockey);
                }

                com.swp.hrtms.hrtmsbe.entity.Jockey jockey = new com.swp.hrtms.hrtmsbe.entity.Jockey();
                jockey.setUsername(username);
                jockey.setPassword("password123");
                jockey.setEmail(email);
                jockey.setRole(UserRole.JOCKEY.name());
                jockey.setJockeyName(jockeyName);
                jockey.setYearOfExperience(yearsOfExperience);
                jockey.setAge(age);
                jockey.setProfessionalBio(professionalBio);
                jockey.setStatus(true);
                return (com.swp.hrtms.hrtmsbe.entity.Jockey) userRepository.save(jockey);
        }

        private Tournament findOrCreateTournament(Admin admin) {
                Tournament tournament = tournamentRepository.findAll()
                                .stream()
                                .filter(item -> "Royal Ascot".equals(item.getName()))
                                .findFirst()
                                .orElse(null);

                if (tournament == null) {
                        tournament = new Tournament();
                }

                tournament.setAdmin(admin);
                tournament.setName("Royal Ascot");
                tournament.setStartDate(java.time.LocalDate.of(2026, 6, 23));
                tournament.setEndDate(java.time.LocalDate.of(2026, 6, 26));
                tournament.setAllowedBreed("Thoroughbred");
                tournament.setAllowedHorseAge(4);
                tournament.setDescription("Royal Ascot mock tournament for race creation API flow.");
                tournament.setStatus("DRAFT");
                return tournamentRepository.save(tournament);
        }

        private Race findOrCreateRace(
                        Tournament tournament,
                        Referee referee,
                        String raceName,
                        java.time.LocalDate date,
                        java.time.LocalTime startTime,
                        java.time.LocalTime endTime,
                        String status) {
                Race race = raceRepository.findAll()
                                .stream()
                                .filter(item -> raceName.equals(item.getName()))
                                .findFirst()
                                .orElse(null);

                if (race == null) {
                        race = new Race();
                }

                race.setTournament(tournament);
                race.setReferee(referee);
                race.setName(raceName);
                race.setDate(date);
                race.setStartTime(startTime);
                race.setEndTime(endTime);
                // race.setLaps(4);
                // race.setNumHorse(10);
                race.setStatus(status);
                race.setTrack("Ascot Racecourse");
                return raceRepository.save(race);
        }

        private void findOrCreateJockeyCertificate(
                        com.swp.hrtms.hrtmsbe.entity.Jockey jockey,
                        String certName,
                        String certImageBase64,
                        String status) {
                boolean exists = jockeyCertRepository.findAll()
                                .stream()
                                .anyMatch(cert -> cert.getJockey() != null
                                                && cert.getJockey().getId().equals(jockey.getId())
                                                && certName.equals(cert.getCertName()));

                if (exists) {
                        return;
                }

                com.swp.hrtms.hrtmsbe.entity.JockeyCert cert = new com.swp.hrtms.hrtmsbe.entity.JockeyCert();
                cert.setJockey(jockey);
                cert.setCertName(certName);
                cert.setCertImg(certImageBase64);
                cert.setStatus(status);
                jockeyCertRepository.save(cert);
        }

        private void findOrCreateNotificationRecipient(
                        User sender,
                        User recipient,
                        Race race,
                        String title,
                        String content,
                        String type,
                        String recipientStatus,
                        java.time.LocalDateTime createdAt) {
                boolean exists = notificationRepository.findAll()
                                .stream()
                                .anyMatch(notification -> type.equals(notification.getType())
                                                && title.equals(notification.getTitle())
                                                && content.equals(notification.getContent()));

                if (exists) {
                        return;
                }

                Notification notification = Notification.builder()
                                .sender(sender)
                                .title(title)
                                .content(content)
                                .type(type)
                                .race(race)
                                .createdAt(createdAt)
                                .build();
                notification = notificationRepository.save(notification);

                NotificationRecipient recipientRecord = NotificationRecipient.builder()
                                .notification(notification)
                                .recipient(recipient)
                                .status(recipientStatus)
                                .build();

                if (createdAt != null && "READ".equals(recipientStatus)) {
                        recipientRecord.setReadAt(createdAt.plusHours(1));
                }

                notificationRecipientRepository.save(recipientRecord);
        }
}