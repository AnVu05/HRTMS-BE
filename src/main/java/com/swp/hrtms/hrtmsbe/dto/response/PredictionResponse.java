package com.swp.hrtms.hrtmsbe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("spectatorId")
    private Integer spectatorId;

    @JsonProperty("raceId")
    private Integer raceId;

    @JsonProperty("predictedHorseId")
    private Integer predictedHorseId;

    @JsonProperty("raceName")
    private String raceName;

    @JsonProperty("predictedHorseName")
    private String predictedHorseName;

    private Integer pointsInvested;
    private com.swp.hrtms.hrtmsbe.enums.PredictionStatus status;
    private LocalDateTime createdAt;

    @JsonProperty("spectator_id")
    public Integer getSpectatorIdSnake() {
        return spectatorId;
    }

    @JsonProperty("race_id")
    public Integer getRaceIdSnake() {
        return raceId;
    }

    @JsonProperty("predicted_horse_id")
    public Integer getPredictedHorseIdSnake() {
        return predictedHorseId;
    }

    @JsonProperty("race_name")
    public String getRaceNameSnake() {
        return raceName;
    }

    @JsonProperty("predicted_horse_name")
    public String getPredictedHorseNameSnake() {
        return predictedHorseName;
    }
}