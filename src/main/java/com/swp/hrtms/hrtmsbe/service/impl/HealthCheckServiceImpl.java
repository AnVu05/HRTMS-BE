package com.swp.hrtms.hrtmsbe.service.impl;

// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.entity.HealthCheck;
import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.repository.HealthCheckRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.DoctorRepository;
import com.swp.hrtms.hrtmsbe.service.HealthCheckService;
import com.swp.hrtms.hrtmsbe.service.RaceService;
import com.swp.hrtms.hrtmsbe.dto.request.HealthCheckRequest;
import com.swp.hrtms.hrtmsbe.dto.response.HealthCheckResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    private final HealthCheckRepository healthCheckRepository;
    private final RegistrationFormRepository registrationFormRepository;
    private final RaceRepository raceRepository;
    private final RaceService raceService;
    private final DoctorRepository doctorRepository;
    private final com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository;
    private final com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository;

    public HealthCheckServiceImpl(HealthCheckRepository healthCheckRepository,
            RegistrationFormRepository registrationFormRepository,
            RaceRepository raceRepository,
            RaceService raceService,
            DoctorRepository doctorRepository,
            com.swp.hrtms.hrtmsbe.repository.UserRepository userRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRepository notificationRepository,
            com.swp.hrtms.hrtmsbe.repository.NotificationRecipientRepository notificationRecipientRepository) {
        this.healthCheckRepository = healthCheckRepository;
        this.registrationFormRepository = registrationFormRepository;
        this.raceRepository = raceRepository;
        this.raceService = raceService;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
    }

    @Override
    @Transactional
    public HealthCheckResponse create(HealthCheckRequest request) {
        RegistrationForm form = registrationFormRepository.findById(request.getRegistrationFormId())
                .orElseThrow(() -> new IllegalArgumentException("Registration form not found"));

        Race race = form.getRace();
        if (race == null) {
            throw new IllegalArgumentException("Race not found");
        }

        // BR_12: Pre-match medical check-up no later than 24h before
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime raceStartDateTime = LocalDateTime.of(race.getDate(), race.getStartTime());
        if (now.isAfter(raceStartDateTime.minusHours(24))) {
            throw new IllegalArgumentException(
                    "Health checks must be updated no later than 24 hours before the race begins.");
        }

        HealthCheck check = HealthCheck.builder()
                .registrationForm(registrationFormRepository.getReferenceById(request.getRegistrationFormId()))
                .doctor(doctorRepository.getReferenceById(request.getDoctorId()))
                .status(request.getStatus())
                .medicalNotes(request.getMedicalNotes())
                .checkDate(request.getCheckDate() != null ? request.getCheckDate() : now)
                .build();

        check = healthCheckRepository.save(check);

        if (com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.PENDING_DOCTOR.equals(request.getStatus())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
            registrationFormRepository.save(form);

            // Send DOCTOR_INVITATION
            com.swp.hrtms.hrtmsbe.entity.User admin = form.getAdmin();
            com.swp.hrtms.hrtmsbe.entity.User doctor = userRepository.findById(request.getDoctorId()).orElse(null);
            if (admin != null && doctor != null) {
                com.swp.hrtms.hrtmsbe.entity.Notification notif = com.swp.hrtms.hrtmsbe.entity.Notification.builder()
                        .sender(admin)
                        .title("Doctor Invitation")
                        .content("You have been assigned to check horse ID: "
                                + (form.getHorse() != null ? form.getHorse().getId() : null))
                        .type(com.swp.hrtms.hrtmsbe.enums.NotificationType.DOCTOR_INVITATION)
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
        }

        // khai
        // BR_13: Health Check logic
        if ("REJECT".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED);
            registrationFormRepository.save(form);

            sendHealthCheckFailedToOwner(form, request.getDoctorId());
            walkOverRaceIfInsufficientRemainingHorses(race);
        } else if ("ACCEPT".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
            registrationFormRepository.save(form);
            sendReadyRacingToOwner(form, request.getDoctorId());
        } else if (com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.PENDING_DOCTOR.equals(request.getStatus())) {
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

        RegistrationForm form = check.getRegistrationForm();
        if (form == null) {
            throw new IllegalArgumentException("Registration form not found");
        }

        Race race = form.getRace();
        if (race == null) {
            throw new IllegalArgumentException("Race not found");
        }

        // BR_12
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime raceStartDateTime = LocalDateTime.of(race.getDate(), race.getStartTime());
        if (now.isAfter(raceStartDateTime.minusHours(24))) {
            throw new IllegalArgumentException(
                    "Health checks must be updated no later than 24 hours before the race begins.");
        }

        if ("DECLINE_INVITATION".equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            check.setDoctor(null);
            check.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.PENDING_DOCTOR);
        } else if ("ACCEPT_INVITATION"
                .equalsIgnoreCase(request.getStatus() == null ? "" : request.getStatus().name())) {
            check.setDoctor(doctorRepository.getReferenceById(request.getDoctorId()));
            check.setStatus(com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.CHECKING);
        } else {
            check.setDoctor(doctorRepository.getReferenceById(request.getDoctorId()));
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
            // Mark DOCTOR_INVITATION as DONE
            Integer adminId = form.getAdmin() != null ? form.getAdmin().getId() : null;
            notificationRepository.updateDoctorInvitationToDone(adminId, request.getDoctorId());

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

            // Mark DOCTOR_INVITATION as DONE
            Integer adminId = form.getAdmin() != null ? form.getAdmin().getId() : null;
            notificationRepository.updateDoctorInvitationToDone(adminId, request.getDoctorId());

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
                walkOverRaceIfInsufficientRemainingHorses(race);
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
        } else if (com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.PENDING_DOCTOR.equals(request.getStatus())) {
            form.setStatus(com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.HEALTH_CHECKING);
            registrationFormRepository.save(form);
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

    // khai
    // BR_15: Auto cancel if < 2 PREPARE forms. Run periodically or trigger
    @Scheduled(fixedRate = 1800000) // Every 30 mins
    @Transactional
    public void autoCancelRaces() {
        LocalDateTime cutoff = LocalDateTime.now().plusHours(24);
        // Find races starting in exactly 24 hours (or between 23.5 and 24 hours from
        // now)
        List<Race> upcomingRaces = raceRepository.findAll().stream()
                .filter(r -> "PUBLISHED".equals(r.getStatus() == null ? "" : r.getStatus().name())
                        || "PENDING_REFEREE".equals(r.getStatus() == null ? "" : r.getStatus().name()))
                .filter(r -> {
                    if (r.getDate() == null || r.getStartTime() == null)
                        return false;
                    LocalDateTime startDateTime = LocalDateTime.of(r.getDate(), r.getStartTime());
                    return startDateTime.isBefore(cutoff) && startDateTime.isAfter(LocalDateTime.now());
                })
                .collect(Collectors.toList());

        for (Race race : upcomingRaces) {
            List<RegistrationForm> passedForms = registrationFormRepository.findByRace_IdAndStatus(race.getId(),
                    com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.RACING);
            if (passedForms.size() < 2) {
                // BR_15
                raceService.walkOverRace(race.getId());
            }
        }
    }

    @Scheduled(fixedRate = 3600000) // run every hour
    @Transactional
    public void rejectExpiredPendingDoctorForms() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<HealthCheck> expiredChecks = healthCheckRepository.findByStatusAndCheckDateBefore(
                com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.PENDING_DOCTOR, cutoff);

        for (HealthCheck check : expiredChecks) {
            if (check.getDoctor() == null)
                continue;

            RegistrationForm form = check.getRegistrationForm();
            if (form == null)
                continue;

            Integer adminId = form.getAdmin() != null ? form.getAdmin().getId() : null;
            Integer doctorId = check.getDoctor().getUserId();

            // Mark DOCTOR_INVITATION as DONE
            notificationRepository.updateDoctorInvitationToDone(adminId, doctorId);

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

            // Keep status PENDING_DOCTOR but clear the doctor ID
            check.setDoctor(null);
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

    private void walkOverRaceIfInsufficientRemainingHorses(Race race) {
        if (race == null || race.getId() == null) {
            return;
        }
        if (race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.WALK_OVER
                || race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED
                || race.getStatus() == com.swp.hrtms.hrtmsbe.enums.RaceStatus.DELETE) {
            return;
        }

        long remainingForms = registrationFormRepository.findByRace_Id(race.getId()).stream()
                .filter(form -> form.getStatus() != com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DISQUALIFIED)
                .filter(form -> form.getStatus() != com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.DELETE)
                .count();
        if (remainingForms < 2) {
            raceService.walkOverRace(race.getId());
        }
    }

}
