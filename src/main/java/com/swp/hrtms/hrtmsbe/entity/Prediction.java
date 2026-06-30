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
    private Integer spectatorId;
    private Integer raceId;
    private Integer predictedHorseId;
    private Integer pointsInvested;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.PredictionStatus status = com.swp.hrtms.hrtmsbe.enums.PredictionStatus.PENDING;
    private java.time.LocalDateTime createdAt;
}
