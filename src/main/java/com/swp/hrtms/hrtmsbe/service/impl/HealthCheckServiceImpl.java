package com.swp.hrtms.hrtmsbe.service.impl;

// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.HealthCheck;
import com.swp.hrtms.hrtmsbe.entity.RacePlacement;
import com.swp.hrtms.hrtmsbe.entity.RaceResult;
import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.repository.HealthCheckRepository;
import com.swp.hrtms.hrtmsbe.repository.RacePlacementRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceResultRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.DoctorRepository;
import com.swp.hrtms.hrtmsbe.service.HealthCheckService;
import com.swp.hrtms.hrtmsbe.service.RaceService;
import com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus;
import com.swp.hrtms.hrtmsbe.dto.request.HealthCheckRequest;
import com.swp.hrtms.hrtmsbe.dto.request.HealthCheckCreateRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HealthCheckResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.health.contributor.Health;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private final HealthCheckRepository healthCheckRepository;
    private final RegistrationFormRepository registrationFormRepository;
    private final RaceRepository raceRepository;
    private final RaceResultRepository raceResultRepository;
    private final RacePlacementRepository racePlacementRepository;
    private final RaceService raceService;
    private final DoctorRepository doctorRepository;
    private final com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository;

    public HealthCheckServiceImpl(HealthCheckRepository healthCheckRepository,
            RegistrationFormRepository registrationFormRepository,
            RaceRepository raceRepository,
            RaceResultRepository raceResultRepository,
            RacePlacementRepository racePlacementRepository,
            RaceService raceService,
            DoctorRepository doctorRepository,
            com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository) {
        this.healthCheckRepository = healthCheckRepository;
        this.registrationFormRepository = registrationFormRepository;
        this.raceRepository = raceRepository;
        this.raceResultRepository = raceResultRepository;
        this.racePlacementRepository = racePlacementRepository;
        this.raceService = raceService;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    @Override
    @Transactional
    public HealthCheckResponse create(HealthCheckCreateRequest request) {
        RegistrationForm form = registrationFormRepository.findById(request.getRegistrationFormId())
                .orElseThrow(() -> new IllegalArgumentException("Registration form not found"));

        if (healthCheckRepository.existsByRegistrationForm_Id(request.getRegistrationFormId())) {
            throw new IllegalArgumentException("Health check already exists for this registration form.");
        }

        Race race = form.getRace();
        if (race == null) {
            throw new IllegalArgumentException("Race not found");
        }

        // BR_12: Pre-match medical check-up no later than 24h before
        // Temporarily disabled for Swagger testing with manually entered checkDate.
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime raceStartDateTime = LocalDateTime.of(race.getDate(), race.getStartTime());
        if (now.isAfter(raceStartDateTime.minusHours(24))) {
            throw new IllegalArgumentException(
                    "Health checks must be updated no later than 24 hours before the race begins.");
        }

        HealthCheckStatus status = resolveCreateStatus(request);

        HealthCheck check = HealthCheck.builder()
                .registrationForm(registrationFormRepository.getReferenceById(request.getRegistrationFormId()))
                .doctor(request.getDoctorId() != null ? doctorRepository.getReferenceById(request.getDoctorId()) : null)
                .status(status)
                .medicalNotes(request.getMedicalNotes())
                .checkDate(request.getCheckDate() != null ? request.getCheckDate() : now)
                .build();

        check = healthCheckRepository.save(check);

        if (HealthCheckStatus.DOCTOR_INVITED.equals(status)) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
            registrationFormRepository.save(form);
            sendDoctorInvitation(form, request.getDoctorId(), race);
        }

        // khai
        // BR_13: Health Check logic
        if (HealthCheckStatus.REJECT.equals(status)) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED);
            registrationFormRepository.save(form);

            sendHealthCheckFailedToOwner(form, request.getDoctorId());
            evaluateRaceAfterHealthCheckReject(race);
        } else if (HealthCheckStatus.ACCEPT.equals(status)) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
            registrationFormRepository.save(form);
            sendReadyRacingToOwner(form, request.getDoctorId());
            finalizeWalkOverIfRemainingHorsePassed(race, form);
        } else if (HealthCheckStatus.PENDING_DOCTOR.equals(status)) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
            registrationFormRepository.save(form);
        }

        return toResponse(check);
    }

    @Override
    public List<HealthCheckResponse> getAll() {
        return healthCheckRepository.findAll().stream()
                .filter(c -> c.getStatus() != com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.DELETE)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HealthCheckResponse getById(Integer id) {
        HealthCheck check = healthCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health check not found"));
        if (check.getStatus() == com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.DELETE) {
            throw new IllegalArgumentException("Health check not found");
        }
        return toResponse(check);
    }

    @Override
    @Transactional
    public HealthCheckResponse update(Integer id, HealthCheckRequest request) {
        HealthCheck check = healthCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health check not found"));
        if (check.getStatus() == com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.DELETE) {
            throw new IllegalArgumentException("Health check not found");
        }
        if (check.getStatus() == com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.ACCEPT
                || check.getStatus() == com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.REJECT) {
            throw new IllegalArgumentException("Completed health checks cannot be updated.");
        }

        RegistrationForm form = check.getRegistrationForm();
        if (form == null) {
            throw new IllegalArgumentException("Registration form not found");
        }
        if (request.getRegistrationFormId() != null && !request.getRegistrationFormId().equals(form.getId())) {
            throw new IllegalArgumentException("Registration form id does not match this health check.");
        }

        Race race = form.getRace();
        if (race == null) {
            throw new IllegalArgumentException("Race not found");
        }

        // BR_12
        // Temporarily disabled for Swagger testing with manually entered checkDate.
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime raceStartDateTime = LocalDateTime.of(race.getDate(), race.getStartTime());
        if (now.isAfter(raceStartDateTime.minusHours(24))) {
            throw new IllegalArgumentException(
                    "Health checks must be updated no later than 24 hours before the race begins.");
        }

        if ("DECLINE_INVITATION".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            check.setDoctor(null);
            check.setStatus(HealthCheckStatus.PENDING_DOCTOR);
        } else if ("ACCEPT_INVITATION"
                .equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            check.setDoctor(doctorRepository.getReferenceById(request.getDoctorId()));
            check.setStatus(HealthCheckStatus.CHECKING);
        } else if (HealthCheckStatus.PENDING_DOCTOR.equals(request.getStatus())
                || HealthCheckStatus.DOCTOR_INVITED.equals(request.getStatus())) {
            if (request.getDoctorId() == null) {
                check.setDoctor(null);
                check.setStatus(HealthCheckStatus.PENDING_DOCTOR);
            } else {
                check.setDoctor(doctorRepository.getReferenceById(request.getDoctorId()));
                check.setStatus(HealthCheckStatus.DOCTOR_INVITED);
            }
        } else {
            if (request.getDoctorId() != null) {
                check.setDoctor(doctorRepository.getReferenceById(request.getDoctorId()));
            }
            check.setStatus(request.getStatus());
        }
        check.setMedicalNotes(request.getMedicalNotes());
        if (request.getCheckDate() != null) {
            check.setCheckDate(request.getCheckDate());
        } else {
            check.setCheckDate(now);
        }
        check = healthCheckRepository.save(check);

        // BR_13: Health Check logic
        if ("DECLINE_INVITATION".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            markDoctorInvitationDone(request, form);

            // Send DECLINE_INVITATION to Admin
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            com.swp.hrtms.hrtmsbe.entity.User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            if (admin != null && doctor != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(doctor)
                        .title("Doctor Declined Invitation")
                        .content("Doctor has declined to participate in checking horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.DECLINE_INVITATION)
                        .build();
                notif = notificationRepository.save(notif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif)
                        .recipient(admin)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec);
            }
        } else if ("ACCEPT_INVITATION"
                .equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
            registrationFormRepository.save(form);

            markDoctorInvitationDone(request, form);

            // Send DOCTOR_ACCEPTED to Admin
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            com.swp.hrtms.hrtmsbe.entity.User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            if (admin != null && doctor != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(doctor)
                        .title("Doctor Accepted Invitation")
                        .content("Doctor has agreed to check horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_ACCEPTED)
                        .build();
                notif = notificationRepository.save(notif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif)
                        .recipient(admin)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec);
            }
        } else if ("REJECT".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED);
            registrationFormRepository.save(form);

            // Send DOCTOR_REJECTED to Admin (Health Check Failed)
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            com.swp.hrtms.hrtmsbe.entity.User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            if (admin != null && doctor != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(doctor)
                        .title("Health Check Failed")
                        .content("Horse ID: " + (form.getHorse() != null ? form.getHorse().getId() : null)
                                + " failed the health check and has been disqualified.")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_REJECTED)
                        .build();
                notif = notificationRepository.save(notif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif)
                        .recipient(admin)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec);
            }

            // Missing logic: Remove horse from prediction form -> Notification -> If total
            // horse in race < 2 -> walk over (cancel)

            // Send Notification to Owner
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            if (doctor != null && owner != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notifOwner = com.swp.hrtms.hrtmsbe.entity.Notification
                        .builder()
                        .sender(doctor)
                        .title("Health Check Failed")
                        .content("Your horse ID: " + (form.getHorse() != null ? form.getHorse().getId() : null)
                                + " failed the health check and has been disqualified.")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.REGISTRATION_REJECTED)
                        .build();
                notifOwner = notificationRepository.save(notifOwner);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient recOwner = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notifOwner)
                        .recipient(owner)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(recOwner);
            }

            if (race != null) {
                evaluateRaceAfterHealthCheckReject(race);
            }
        } else if ("ACCEPT".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
            registrationFormRepository.save(form);

            // Send READY_RACING to owner.
            com.swp.hrtms.hrtmsbe.entity.User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
            if (owner != null && doctor != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(doctor)
                        .title("Ready Racing")
                        .content("Your horse ID: " + (form.getHorse() != null ? form.getHorse().getId() : null)
                                + " passed the health check and is ready for the race.")
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.READY_RACING)
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
            finalizeWalkOverIfRemainingHorsePassed(race, form);
        } else if (HealthCheckStatus.PENDING_DOCTOR.equals(request.getStatus())
                || HealthCheckStatus.DOCTOR_INVITED.equals(request.getStatus())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
            registrationFormRepository.save(form);
            if (request.getDoctorId() != null) {
                sendDoctorInvitation(form, request.getDoctorId(), race);
            }
        }

        return toResponse(check);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        HealthCheck check = healthCheckRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Health check not found"));
        if (check.getStatus() == com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.DELETE) {
            throw new IllegalArgumentException("Health check not found");
        }
        // khai
        check.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.DELETE);
        healthCheckRepository.save(check);
    }

    @Scheduled(fixedRate = 3600000) // run every hour
    @Transactional
     //Health check quá hạn doctor không phản hồi
    public void rejectExpiredPendingDoctorForms() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<HealthCheck> expiredChecks = healthCheckRepository.findByStatusAndCheckDateBefore(
                HealthCheckStatus.DOCTOR_INVITED, cutoff);

        for (HealthCheck check : expiredChecks) {
            if (check.getDoctor() == null)
                continue;

            RegistrationForm form = check.getRegistrationForm();
            if (form == null)
                continue;

            Integer adminId = form.getAdmin() != null ? form.getAdmin().getId() : null;
            Integer doctorId = check.getDoctor().getUserId();

            // Mark DOCTOR_INVITATION as DONE
            notificationRepository.updateDoctorInvitationToDoneByRegistrationForm(adminId, doctorId, form.getId());

            // Send expiration notification to Admin
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            if (admin != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(check.getDoctor().getUser())
                        .title("Invitation Expired")
                        .content("Doctor invitation has expired because the doctor did not respond for horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.DECLINE_INVITATION)
                        .build();
                notif = notificationRepository.save(notif);

                com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                        .builder()
                        .notification(notif)
                        .recipient(admin)
                        .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                        .build();
                notificationRecipientRepository.save(rec);
            }

            // Move back to PENDING_DOCTOR so admin can assign another doctor.
            check.setDoctor(null);
            check.setStatus(HealthCheckStatus.PENDING_DOCTOR);
            check.setCheckDate(LocalDateTime.now()); // reset check date for next doctor
        }
        healthCheckRepository.saveAll(expiredChecks);
    }

    private HealthCheckResponse toResponse(HealthCheck check) {
        return HealthCheckResponse.builder()
                .id(check.getId())
                .registrationFormId(check.getRegistrationForm() != null ? check.getRegistrationForm().getId() : null)
                .doctorId(check.getDoctor() != null ? check.getDoctor().getUserId() : null)
                .status(check.getStatus())
                .medicalNotes(check.getMedicalNotes())
                .checkDate(check.getCheckDate())
                .build();
    }

    private void markDoctorInvitationDone(HealthCheckRequest request, RegistrationForm form) {
        Integer adminId = form.getAdmin() != null ? form.getAdmin().getId() : null;
        Integer registrationFormId = form.getId();
        if (adminId == null || registrationFormId == null) {
            throw new IllegalArgumentException("Cannot resolve doctor invitation without admin and registration form context.");
        }
        int updated = notificationRepository.updateDoctorInvitationToDoneByRegistrationForm(
                adminId,
                request.getDoctorId(),
                registrationFormId);
        if (updated == 0) {
            throw new IllegalArgumentException("Doctor invitation not found or already responded.");
        }
    }

    private HealthCheckStatus resolveCreateStatus(HealthCheckCreateRequest request) {
        if (request.getStatus() == HealthCheckStatus.ACCEPT || request.getStatus() == HealthCheckStatus.REJECT) {
            return request.getStatus();
        }
        return request.getDoctorId() == null ? HealthCheckStatus.PENDING_DOCTOR : HealthCheckStatus.DOCTOR_INVITED;
    }

    private void sendDoctorInvitation(RegistrationForm form, Integer doctorId, Race race) {
        com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
        com.swp.hrtms.hrtmsbe.entity.User doctor = doctorId != null ? userRepository.findById(doctorId).orElse(null) : null;
        if (admin == null || doctor == null) {
            return;
        }

        com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                .sender(admin)
                .title("Doctor Invitation")
                .content("You have been assigned to check horse ID: "
                        + (form.getHorse() != null ? form.getHorse().getId() : null))
                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_INVITATION)
                .race(race)
                .registrationForm(form)
                .build();
        notif = notificationRepository.save(notif);

        com.swp.hrtms.hrtmsbe.entity.NotificationRecipient rec = com.swp.hrtms.hrtmsbe.entity.NotificationRecipient
                .builder()
                .notification(notif)
                .recipient(doctor)
                .status(com.swp.hrtms.hrtmsbe.enums.NotificationStatus.UNREAD)
                .build();
        notificationRecipientRepository.save(rec);
    }

    private void sendReadyRacingToOwner(RegistrationForm form, Integer doctorId) {
        com.swp.hrtms.hrtmsbe.entity.User doctor = userRepository.findById(doctorId).orElse(null);
        com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
        if (doctor == null || owner == null) {
            return;
        }

        com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                .sender(doctor)
                .title("Ready Racing")
                .content("Your horse ID: " + (form.getHorse() != null ? form.getHorse().getId() : null)
                        + " passed the health check and is ready for the race.")
                .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.READY_RACING)
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

    private void sendHealthCheckFailedToOwner(RegistrationForm form, Integer doctorId) {
        com.swp.hrtms.hrtmsbe.entity.User doctor = userRepository.findById(doctorId).orElse(null);
        com.swp.hrtms.hrtmsbe.entity.User owner = form.getOwner() != null ? form.getOwner().getUser() : null;
        if (doctor == null || owner == null) {
            return;
        }

        com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                .sender(doctor)
                .title("Health Check Failed")
                .content("Your horse ID: " + (form.getHorse() != null ? form.getHorse().getId() : null)
                        + " failed the health check and has been disqualified.")
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

    // Evaluates race eligibility after a health-check reject and marks WALK_OVER or CANCELLED without creating results early.
    private void evaluateRaceAfterHealthCheckReject(Race race) {
        if (race == null || race.getId() == null) {
            return;
        }
        if (race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED
                || race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.DELETE) {
            return;
        }

        List<RegistrationForm> eligibleForms = getEligibleForms(race);
        if (eligibleForms.size() == 1) {
            raceService.walkOverRace(race.getId());
            finalizeWalkOverIfRemainingHorsePassed(race, eligibleForms.get(0));
        } else if (eligibleForms.isEmpty()) {
            race.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED);
            race.setReason("Race cancelled because all horses failed health check.");
            race.setCanceledAt(LocalDateTime.now());
            raceRepository.save(race);
        }
    }

    // Finalizes a walk-over only after the last eligible horse has passed health check.
    private void finalizeWalkOverIfRemainingHorsePassed(Race race, RegistrationForm acceptedForm) {
        if (race == null || race.getId() == null || acceptedForm == null || acceptedForm.getId() == null) {
            return;
        }
        if (race.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.WALK_OVER) {
            return;
        }

        List<RegistrationForm> eligibleForms = getEligibleForms(race);
        if (eligibleForms.size() != 1 || !eligibleForms.get(0).getId().equals(acceptedForm.getId())) {
            return;
        }
        if (acceptedForm.getStatus() != com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        RaceResult result = raceResultRepository.findByRace_Id(race.getId()).orElse(null);
        if (result == null) {
            result = RaceResult.builder()
                    .race(race)
                    .referee(race.getReferee())
                    .status(com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL)
                    .createdAt(now)
                    .photoFinishImage("WALK_OVER")
                    .build();
        } else {
            result.setReferee(race.getReferee());
            result.setStatus(com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.OFFICIAL);
            result.setPhotoFinishImage("WALK_OVER");
            if (result.getCreatedAt() == null) {
                result.setCreatedAt(now);
            }
        }
        result = raceResultRepository.save(result);

        boolean placementExists = racePlacementRepository.findByRaceResult_Id(result.getId()).stream()
                .anyMatch(placement -> placement.getRegistrationForm() != null
                        && placement.getRegistrationForm().getId().equals(acceptedForm.getId()));
        if (!placementExists) {
            racePlacementRepository.save(RacePlacement.builder()
                    .raceResult(result)
                    .registrationForm(acceptedForm)
                    .finishPosition(1)
                    .finishTime(now)
                    .build());
        }

        acceptedForm.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.COMPLETE);
        registrationFormRepository.save(acceptedForm);
    }

    // Returns forms that are still eligible to keep a race alive after health checks.
    private List<RegistrationForm> getEligibleForms(Race race) {
        return registrationFormRepository.findByRace_Id(race.getId()).stream()
                .filter(form -> form.getStatus() != com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED)
                .filter(form -> form.getStatus() != com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DELETE)
                .toList();
    }

}
