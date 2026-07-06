package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spectator_id")
    private Spectator spectator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predicted_horse_id")
    private Horse predictedHorse;
    private Integer pointsInvested;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.PredictionStatus status = com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING;
    private java.time.LocalDateTime createdAt;
}
