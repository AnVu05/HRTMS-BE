package com.swp.hrtms.hrtmsbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceUpdateTimeRequest {

    private LocalDate date;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;
}

