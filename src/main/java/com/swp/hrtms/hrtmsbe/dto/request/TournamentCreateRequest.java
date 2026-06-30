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
public class TournamentCreateRequest {

    private String name;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("published_date")
    private LocalDate publishedDate;

    @JsonProperty("open_prediction_date")
    private LocalDate openPredictionDate;

    @JsonProperty("close_prediction_date")
    private LocalDate closePredictionDate;

    // khai
    @JsonProperty("allowed_breed")
    private String allowedBreed;

    @JsonProperty("allowed_horse_age")
    private Integer allowedHorseAge;

    @JsonProperty("tournament_description")
    private String description;

    private com.swp.hrtms.hrtmsbe.enums.TournamentStatus status;

    @JsonProperty("cancel_reason")
    private String cancelReason;
}
