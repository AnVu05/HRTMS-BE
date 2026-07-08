package com.swp.hrtms.hrtmsbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentResponse {

    private Integer id;

    @JsonProperty("admin_id")
    private Integer adminId;

    private String name;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

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

    private com.swp.hrtms.hrtmsbe.enums.TournamentStatus status;

    @JsonProperty("canceled_at")
    private LocalDateTime canceledAt;

    private String reason;
}

