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

    private Integer id;

    @JsonProperty("admin_id")
    private Integer adminId;

    private String name;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("allowed_breed")
    private String allowedBreed;

    @JsonProperty("allowed_horse_age")
    private Integer allowedHorseAge;

    private String status;
}
