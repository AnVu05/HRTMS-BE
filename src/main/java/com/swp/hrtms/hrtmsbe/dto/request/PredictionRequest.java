package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequest {
    private Integer spectatorId;
    private Integer raceId;
    private Integer predictedHorseId;
    private Integer pointsInvested;
    //khai
    private com.swp.hrtms.hrtmsbe.enums.PredictionStatus status;
    private LocalDateTime createdAt;
}