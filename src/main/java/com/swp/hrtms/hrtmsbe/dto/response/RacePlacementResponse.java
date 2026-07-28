package com.swp.hrtms.hrtmsbe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RacePlacementResponse {
    private Integer id;

    @JsonProperty("raceResultId")
    private Integer raceResultId;

    @JsonProperty("registrationFormId")
    private Integer registrationFormId;

    @JsonProperty("jockeyName")
    private String jockeyName;

    @JsonProperty("horseName")
    private String horseName;

    @JsonProperty("finishPosition")
    private Integer finishPosition;

    @JsonProperty("finishTime")
    private LocalDateTime finishTime;

    @JsonProperty("weighInWeight")
    private Double weighInWeight;

    @JsonProperty("race_result_id")
    public Integer getRaceResultIdSnake() {
        return raceResultId;
    }

    @JsonProperty("registration_form_id")
    public Integer getRegistrationFormIdSnake() {
        return registrationFormId;
    }

    @JsonProperty("jockey_name")
    public String getJockeyNameSnake() {
        return jockeyName;
    }

    @JsonProperty("horse_name")
    public String getHorseNameSnake() {
        return horseName;
    }

    @JsonProperty("finish_position")
    public Integer getFinishPositionSnake() {
        return finishPosition;
    }

    @JsonProperty("finish_time")
    public LocalDateTime getFinishTimeSnake() {
        return finishTime;
    }

    @JsonProperty("weigh_in_weight")
    public Double getWeighInWeightSnake() {
        return weighInWeight;
    }
}



