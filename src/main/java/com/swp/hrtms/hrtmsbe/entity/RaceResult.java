package com.swp.hrtms.hrtmsbe.entity;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raceresults")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referee_id")
    private Referee referee;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.RaceResultStatus status = com.swp.hrtms.hrtmsbe.enums.RaceResultStatus.TEMPORARY;
    private java.time.LocalDateTime createdAt;
}





