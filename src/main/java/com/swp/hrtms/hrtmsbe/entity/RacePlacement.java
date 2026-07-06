package com.swp.hrtms.hrtmsbe.entity;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raceplacements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RacePlacement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_result_id")
    private RaceResult raceResult;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_form_id")
    private RegistrationForm registrationForm;
    private Integer finishPosition;
    private java.time.LocalDateTime finishTime;
    private Double weighInWeight;
}



