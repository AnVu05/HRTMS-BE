package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "healthchecks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_form_id")
    private RegistrationForm registrationForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus status = com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus.PENDING_DOCTOR;
    private String medicalNotes;
    private java.time.LocalDateTime checkDate;
}





