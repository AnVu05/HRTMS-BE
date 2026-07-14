package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.entity.Horse;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.RaceFormat;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.JockeyRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.repository.AdminRepository;
import com.swp.hrtms.hrtmsbe.service.RegistrationFormService;
import com.swp.hrtms.hrtmsbe.dto.request.RegistrationFormRequest;
import com.swp.hrtms.hrtmsbe.dto.request.JockeyRespondRequest;
import com.swp.hrtms.hrtmsbe.dto.response.RegistrationFormResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrationFormServiceImpl implements RegistrationFormService {

    private final RegistrationFormRepository repository;
    private final HorseRepository horseRepository;
    private final RaceRepository raceRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository;
    private final com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository;
    private final HorseOwnerRepository horseOwnerRepository;
    private final JockeyRepository jockeyRepository;
    private final TournamentRepository tournamentRepository;
    private final AdminRepository adminRepository;

    public RegistrationFormServiceImpl(RegistrationFormRepository repository,
            HorseRepository horseRepository,
            RaceRepository raceRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository,
            com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository,
            HorseOwnerRepository horseOwnerRepository,
            JockeyRepository jockeyRepository,
            TournamentRepository tournamentRepository,
            AdminRepository adminRepository) {
        this.repository = repository;
        this.horseRepository = horseRepository;
        this.raceRepository = raceRepository;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
        this.userRepository = userRepository;
        this.horseOwnerRepository = horseOwnerRepository;
        this.jockeyRepository = jockeyRepository;
        this.tournamentRepository = tournamentRepository;
        this.adminRepository = adminRepository;
    }

    @Override
    @Transactional
    public RegistrationFormResponse create(RegistrationFormRequest request) {
        // BR_04: Staffing Limits. Exactly one main horse and one main jockey.
        if (request.getHorseId() == null || request.getJockeyId() == null) {
            throw new IllegalArgumentException("Horse ID and Jockey ID are required.");
        }

        Horse horse = horseRepository.findById(request.getHorseId())
                .orElseThrow(() -> new IllegalArgumentException("Horse not found."));

        Race race = null;
        if (request.getRaceId() != null) {
            race = raceRepository.findById(request.getRaceId())
                    .orElseThrow(() -> new IllegalArgumentException("Race not found."));
        } else if (request.getTournamentId() != null) {
            List<Race> races = raceRepository.findByTournamentId(request.getTournamentId());
            if (!races.isEmpty()) {
                race = races.get(0); // Dựa vào giải đấu lấy cuộc đua mẫu nếu ko truyền cụ thể
            }
        }

        if (race != null && race.getRaceRules() != null) {
            RaceFormat rules = race.getRaceRules();
            // BR_02: Horse physical condition. Block form submissions if age and weight do
            // not match.
            if (horse.getAge() == null || horse.getWeightKg() == null) {
                throw new IllegalArgumentException("Horse age and weight must be fully updated.");
            }
            if (rules.getAllowedHorseAge() != null && horse.getAge() < rules.getAllowedHorseAge()) {
                throw new IllegalArgumentException("Horse age is below the allowed age for this race.");
            }

            // New logic: 1. allowedBreed
            if (rules.getAllowedBreed() != null && !rules.getAllowedBreed().isBlank()) {
                if (horse.getBreed() == null
                        || !horse.getBreed().trim().equalsIgnoreCase(rules.getAllowedBreed().trim())) {
                    throw new IllegalArgumentException("Horse breed does not match the allowed breed for this race.");
                }
            }

            // New logic: 3. minJockeyExperience
            if (rules.getMinJockeyExperience() != null) {
                com.swp.hrtms.hrtmsbe.entity.Jockey jockey = jockeyRepository.findById(request.getJockeyId())
                        .orElseThrow(() -> new IllegalArgumentException("Jockey not found."));
                if (jockey.getExperienceYears() == null
                        || jockey.getExperienceYears() < rules.getMinJockeyExperience()) {
                    throw new IllegalArgumentException(
                            "Jockey does not meet the minimum experience requirement for this race.");
                }
            }

            if (horse.getAge() == null || horse.getBreed() == null || horse.getBreed().isBlank()) {
                throw new IllegalArgumentException("Horse age and breed must be fully updated before registering.");
            }

            // New logic: 4. Check Jockey Certificate matching Horse Breed
            com.swp.hrtms.hrtmsbe.entity.Jockey jockey = jockeyRepository.findById(request.getJockeyId())
                    .orElseThrow(() -> new IllegalArgumentException("Jockey not found."));

            boolean hasValidCert = false;
            if (jockey.getJockeyCerts() != null) {
                for (com.swp.hrtms.hrtmsbe.entity.JockeyCert cert : jockey.getJockeyCerts()) {
                    if (com.swp.hrtms.hrtmsbe.enums.CertificateStatus.VERIFIED == cert.getStatus()
                            && cert.getCertName() != null
                            && cert.getCertName().trim().equalsIgnoreCase(horse.getBreed().trim())) {
                        hasValidCert = true;
                        break;
                    }
                }
            }

            if (!hasValidCert) {
                throw new IllegalArgumentException("Jockey does not have a verified certificate for this horse breed.");
            }
        }
        // check slot register
        if (race != null) {
            // New logic: 5. Check duplicate Jockey in the same race
            if (request.getJockeyId() != null) {
                java.util.List<com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus> jockeyExcludedStatuses = java.util.Arrays
                        .asList(
                                com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED,
                                com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DELETE);

                boolean jockeyAlreadyRegistered = repository.existsByJockey_IdAndRace_IdAndStatusNotIn(
                        request.getJockeyId(), race.getId(), jockeyExcludedStatuses);
                if (jockeyAlreadyRegistered) {
                    throw new IllegalArgumentException(
                            "This jockey is already registered to ride another horse in this race.");
                }
            }

            java.util.List<com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus> excludedStatuses = java.util.Arrays
                    .asList(
                            com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED,
                            com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DELETE,
                            com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.UPDATE);

            boolean alreadyRegistered = repository.existsByHorse_IdAndRace_IdAndStatusNotIn(horse.getId(), race.getId(),
                    excludedStatuses);
            if (alreadyRegistered) {
                throw new IllegalArgumentException("This horse is already registered for this race.");
            }

            long currentCount = repository.countByRace_IdAndStatusNotIn(race.getId(), excludedStatuses);

            if (race.getNumHorse() != null && currentCount >= race.getNumHorse()) {
                if (request.getOwnerId() != null) {
                    com.swp.hrtms.hrtmsbe.entity.User owner = userRepository.findById(request.getOwnerId())
                            .orElse(null);
                    if (owner != null) {
                        com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                                .builder()
                                .sender(null) // SYSTEM
                                .title("Race Registration Failed")
                                .content("There are no more slots left in this race.")
                                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.SYSTEM)
                                .race(race)
                                .createdAt(LocalDateTime.now())
                                .build();
                        notification = notificationRepository.save(notification);

                        com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                                .builder()
                                .notification(notification)
                                .recipient(owner)
                                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                                .build();
                        notificationRecipientRepository.save(recipient);
                    }
                }
                throw new IllegalArgumentException("Race is full. No available slots left.");
            }
        }

        // Find least loaded admin
        com.swp.hrtms.hrtmsbe.entity.Admin leastLoadedAdmin = adminRepository
                .findLeastLoadedAdmin(org.springframework.data.domain.PageRequest.of(0, 1))
                .getContent()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No admins available in the system."));

        // BR_03: Starts as PENDING_JOCKEY
        RegistrationForm form = RegistrationForm.builder()
                .owner(request.getOwnerId() != null ? horseOwnerRepository.getReferenceById(request.getOwnerId())
                        : null)
                .horse(request.getHorseId() != null ? horseRepository.getReferenceById(request.getHorseId()) : null)
                .jockey(request.getJockeyId() != null ? jockeyRepository.getReferenceById(request.getJockeyId()) : null)
                .tournament(request.getTournamentId() != null
                        ? tournamentRepository.getReferenceById(request.getTournamentId())
                        : null)
                .race(race)
                // khai
                .admin(leastLoadedAdmin)
                .status(request.getStatus() != null ? request.getStatus()
                        : com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_JOCKEY)
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now())
                .build();

        form = repository.save(form);

        // Send JOCKEY_INVITATION
        com.swp.hrtms.hrtmsbe.entity.User owner = userRepository.findById(request.getOwnerId()).orElse(null);
        com.swp.hrtms.hrtmsbe.entity.User jockey = userRepository.findById(request.getJockeyId()).orElse(null);
        if (owner != null && jockey != null) {
            com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                    .sender(owner)
                    .title("Jockey Invitation")
                    .content("You have been invited to ride horse ID: " + request.getHorseId())
                    .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.JOCKEY_INVITATION)
                    .race(race)
                    .build();
            notification = notificationRepository.save(notification);

            com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                    .builder()
                    .notification(notification)
                    .recipient(jockey)
                    .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                    .build();
            notificationRecipientRepository.save(recipient);
        }

        return toResponse(form);
    }

    @Override
    @Transactional
    public RegistrationFormResponse jockeyRespond(Integer id, JockeyRespondRequest request) {
        RegistrationForm form = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found."));

        if (form.getStatus() != com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_JOCKEY) {
            throw new IllegalArgumentException("Form is not pending jockey response.");
        }

        if ("Accept".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus())) {
            // khai
            // BR_03: Move to PENDING_ADMIN when accepted
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_ADMIN);

            // Mark JOCKEY_INVITATION as READ then DONE
            Integer ownerId = form.getOwner() != null ? form.getOwner().getUserId() : null;
            Integer jockeyId = form.getJockey() != null ? form.getJockey().getId() : null;
            if (ownerId != null && jockeyId != null) {
                notificationRecipientRepository.markJockeyInvitationAsRead(ownerId, jockeyId);
            }
            notificationRepository.updateJockeyInvitationToDone(ownerId, jockeyId);

            // Send JOCKEY_ACCEPTED to owner
            com.swp.hrtms.hrtmsbe.entity.User jockey = form.getJockey();
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            if (jockey != null && owner != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif1 = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(jockey)
                        .title("Jockey Accepted")
                        .content("Jockey has accepted to ride horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.JOCKEY_ACCEPTED)
                        .build();
                notif1 = notificationRepository.save(notif1);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec1 = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif1)
                        .recipient(owner)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec1);
            }

            // Send REGISTRATION_VERIFY to admin
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            if (owner != null && admin != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif2 = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(owner)
                        .title("Registration Verification")
                        .content("A new registration form is pending verification for horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_VERIFY)
                        .build();
                notif2 = notificationRepository.save(notif2);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec2 = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif2)
                        .recipient(admin)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec2);
            }

        } else if ("Reject".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_JOCKEY);

            // Mark JOCKEY_INVITATION as READ then DONE
            Integer ownerId = form.getOwner() != null ? form.getOwner().getUserId() : null;
            Integer jockeyId = form.getJockey() != null ? form.getJockey().getId() : null;
            if (ownerId != null && jockeyId != null) {
                notificationRecipientRepository.markJockeyInvitationAsRead(ownerId, jockeyId);
            }
            notificationRepository.updateJockeyInvitationToDone(ownerId, jockeyId);

            // Send JOCKEY_REJECTED to owner
            com.swp.hrtms.hrtmsbe.entity.User jockey = form.getJockey();
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            if (jockey != null && owner != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif1 = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(jockey)
                        .title("Jockey Rejected")
                        .content("Jockey has rejected to ride horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.JOCKEY_REJECTED)
                        .build();
                notif1 = notificationRepository.save(notif1);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec1 = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif1)
                        .recipient(owner)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec1);
            }

            // Clear the jockey so the form is ready for a new one
            form.setJockey(null);
        } else {
            throw new IllegalArgumentException("Invalid status. Must be Accept or Reject.");
        }

        form = repository.save(form);
        return toResponse(form);
    }

    @Override
    @Transactional
    public RegistrationFormResponse adminRespond(Integer id,
            com.swp.hrtms.hrtmsbe.dto.request.AdminRespondRequest request) {
        RegistrationForm form = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found."));

        if (form.getStatus() != com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_ADMIN) {
            throw new IllegalArgumentException("Form is not pending admin response.");
        }

        if ("Accept".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus())) {
            // Admin accepted the form info.
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PREPARE);

            // Mark REGISTRATION_VERIFY as READ then DONE
            Integer ownerId = form.getOwner() != null ? form.getOwner().getUserId() : null;
            Integer adminId = form.getAdmin() != null ? form.getAdmin().getId() : null;
            if (ownerId != null && adminId != null) {
                notificationRecipientRepository.markRegistrationVerifyAsRead(ownerId, adminId);
            }
            notificationRepository.updateRegistrationVerifyToDone(ownerId, adminId);

            // Send REGISTRATION_APPROVED
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            if (admin != null && owner != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(admin)
                        .title("Registration Approved")
                        .content("Your registration for horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null) + " has been approved.")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_APPROVED)
                        .build();
                notif = notificationRepository.save(notif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif)
                        .recipient(owner)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec);
            }
        } else if ("Reject".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.UPDATE);

            // Mark REGISTRATION_VERIFY as READ then DONE
            Integer ownerId = form.getOwner() != null ? form.getOwner().getUserId() : null;
            Integer adminId = form.getAdmin() != null ? form.getAdmin().getId() : null;
            if (ownerId != null && adminId != null) {
                notificationRecipientRepository.markRegistrationVerifyAsRead(ownerId, adminId);
            }
            notificationRepository.updateRegistrationVerifyToDone(ownerId, adminId);

            // Send REGISTRATION_REJECTED
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            if (admin != null && owner != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(admin)
                        .title("Registration Rejected")
                        .content("Your registration for horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null)
                                + " has been rejected. Reason: " + request.getReason())
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_REJECTED)
                        .build();
                notif = notificationRepository.save(notif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif)
                        .recipient(owner)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec);
            }
        } else {
            throw new IllegalArgumentException("Invalid status. Must be Accept or Reject.");
        }

        form = repository.save(form);
        return toResponse(form);
    }

    // BR_18: Scheduled task to reject PENDING_JOCKEY applications older than 24h.
    @Scheduled(fixedRate = 3600000) // run every hour
    @Transactional
    public void rejectExpiredPendingJockeyForms() {
        java.time.LocalDateTime cutoff = java.time.LocalDateTime.now().minusHours(24);
        List<RegistrationForm> expiredForms = repository.findByStatusAndCreatedAtBefore(
                com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_JOCKEY, cutoff);
        for (RegistrationForm form : expiredForms) {
            if (form.getJockey() == null)
                continue; // Already expired or rejected, waiting for owner

            // Mark JOCKEY_INVITATION as DONE
            Integer ownerId = form.getOwner() != null ? form.getOwner().getUserId() : null;
            Integer jockeyId = form.getJockey().getId();
            notificationRepository.updateJockeyInvitationToDone(ownerId, jockeyId);

            // Send expiration notification to Owner
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            if (owner != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(form.getJockey()) // The jockey who didn't respond
                        .title("Invitation Expired")
                        .content("Your invitation has expired because the jockey did not respond")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.JOCKEY_REJECTED)
                        .build();
                notif = notificationRepository.save(notif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif)
                        .recipient(owner)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec);
            }

            // Keep status PENDING_JOCKEY but clear the jockey ID
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_JOCKEY);
            form.setJockey(null);
        }
        repository.saveAll(expiredForms);
    }

    @Override
    public List<RegistrationFormResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<RegistrationFormResponse> getPendingAdminForms(Integer adminId) {
        return repository
                .findByAdmin_IdAndStatus(adminId, com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_ADMIN)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<RegistrationFormResponse> getByOwnerId(Integer ownerId) {
        return repository.findByOwner_UserId(ownerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<RegistrationFormResponse> getRacingFormsByRaceId(Integer raceId) {
        return repository.findByRace_IdAndStatus(raceId, com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public RegistrationFormResponse getById(Integer id) {
        RegistrationForm form = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found."));
        return toResponse(form);
    }

    @Override
    @Transactional
    public RegistrationFormResponse update(Integer id, RegistrationFormRequest request) {
        RegistrationForm form = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found."));

        Integer oldJockeyId = form.getJockey() != null ? form.getJockey().getId() : null;
        Integer newJockeyId = request.getJockeyId();

        form.setOwner(
                request.getOwnerId() != null ? horseOwnerRepository.getReferenceById(request.getOwnerId()) : null);
        form.setHorse(request.getHorseId() != null ? horseRepository.getReferenceById(request.getHorseId()) : null);
        form.setJockey(request.getJockeyId() != null ? jockeyRepository.getReferenceById(request.getJockeyId()) : null);
        form.setTournament(
                request.getTournamentId() != null ? tournamentRepository.getReferenceById(request.getTournamentId())
                        : null);
        form.setRace(request.getRaceId() != null ? raceRepository.getReferenceById(request.getRaceId()) : null);

        // If jockey changed, reset expiration timer (Risk 2) and make sure status is
        // PENDING_JOCKEY
        if (newJockeyId != null && !newJockeyId.equals(oldJockeyId)) {
            form.setCreatedAt(java.time.LocalDateTime.now());
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_JOCKEY);
        }

        form = repository.save(form);

        // Send JOCKEY_INVITATION to the new jockey
        if (newJockeyId != null && !newJockeyId.equals(oldJockeyId)) {
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            com.swp.hrtms.hrtmsbe.entity.User newJockey = userRepository.findById(newJockeyId).orElse(null);

            if (owner != null && newJockey != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notification = com.swp.hrtms.hrtmsbe.entity.Notification
                        .builder()
                        .sender(owner)
                        .title("Jockey Invitation")
                        .content("You have been invited to ride horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.JOCKEY_INVITATION)
                        .race(form.getRace())
                        .build();
                notification = notificationRepository.save(notification);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recipient = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notification)
                        .recipient(newJockey)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(recipient);
            }
        }

        return toResponse(form);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // khai
        RegistrationForm form = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Form not found."));
        // khai
        repository.delete(form);
    }

    private RegistrationFormResponse toResponse(RegistrationForm form) {
        return RegistrationFormResponse.builder()
                .id(form.getId())
                .ownerId(form.getOwner() != null ? form.getOwner().getUserId() : null)
                .horseId(form.getHorse() != null ? form.getHorse().getId() : null)
                .jockeyId(form.getJockey() != null ? form.getJockey().getId() : null)
                .tournamentId(form.getTournament() != null ? form.getTournament().getId() : null)
                .raceId(form.getRace() != null ? form.getRace().getId() : null)
                .adminId(form.getAdmin() != null ? form.getAdmin().getId() : null)
                .ownerName(form.getOwner() != null ? form.getOwner().getOwnerName() : null)
                .horseName(form.getHorse() != null ? form.getHorse().getName() : null)
                .jockeyName(form.getJockey() != null ? form.getJockey().getJockeyName() : null)
                .tournamentName(form.getTournament() != null ? form.getTournament().getName() : null)
                .raceName(form.getRace() != null ? form.getRace().getName() : null)
                .status(form.getStatus())
                .createdAt(form.getCreatedAt())
                .build();
    }
}
