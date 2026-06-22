package com.swp.hrtms.hrtmsbe.dto.response;

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
public class RaceResponse {

    private Integer id;

    @JsonProperty("tournament_id")
    private Integer tournamentId;

    private String name;

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

    private String status;

    private String track;
}
