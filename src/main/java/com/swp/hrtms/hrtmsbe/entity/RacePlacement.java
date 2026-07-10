package com.swp.hrtms.hrtmsbe.entity;


import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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



