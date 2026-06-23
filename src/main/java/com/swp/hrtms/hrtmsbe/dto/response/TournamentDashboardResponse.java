package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentDashboardResponse {
    private Integer id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long numRaces;
    private String status;

    @JsonProperty("allowed_breed")
    private String allowedBreed;

    @JsonProperty("allowed_horse_age")
    private Integer allowedHorseAge;

    @JsonProperty("tournament_description")
    private String description;
}
