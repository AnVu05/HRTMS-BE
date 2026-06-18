package com.swp.hrtms.hrtmsbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentUpdateRequest {

    @JsonProperty("tournament_name")
    private String name;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("allowed_horse_breed")
    private String allowedBreed;

    @JsonProperty("horse_age_requirement")
    private Integer allowedHorseAge;

    @JsonProperty("tournament_description")
    private String description;

    private String status;
}
