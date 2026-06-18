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
public class SingleRaceCreateRequest {

    @JsonProperty("tournament_id")
    private Integer tournamentId;

    @JsonProperty("race_name")
    private String raceName;

    private LocalDate date;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    private Integer laps;

    @JsonProperty("num_horse")
    private Integer numHorse;

    @JsonProperty("referee_id")
    private Integer refereeId;
}
