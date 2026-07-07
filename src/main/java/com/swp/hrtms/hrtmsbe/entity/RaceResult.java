package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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





