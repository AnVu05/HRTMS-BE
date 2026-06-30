package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResponse {
    private Integer id;
    private Integer spectatorId;
    private Integer raceId;
    private Integer predictedHorseId;
    private Integer pointsInvested;
    private com.swp.hrtms.hrtmsbe.enums.PredictionStatus status;
    private LocalDateTime createdAt;
}
