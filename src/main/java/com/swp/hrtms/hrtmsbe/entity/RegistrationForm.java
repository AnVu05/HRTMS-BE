package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registrationforms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private HorseOwner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id")
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jockey_id")
    private Jockey jockey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status = com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus.PENDING_JOCKEY;
    private java.time.LocalDateTime createdAt;
}
