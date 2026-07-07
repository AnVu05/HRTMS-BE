package com.swp.hrtms.hrtmsbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceCreateRequest {

    private String name;

    private LocalDate date;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    @JsonProperty("distance_m")
    private Integer distanceM;

    @JsonProperty("num_horse")
    private Integer numHorse;

    @JsonProperty("referee_id")
    private Integer refereeId;

    @JsonProperty("race_rules_id")
    private Integer raceRulesId;

    @JsonProperty("expected_duration_minutes")
    private Integer expectedDurationMinutes;

    @JsonProperty("break_time_minutes")
    private Integer breakTimeMinutes;
}
