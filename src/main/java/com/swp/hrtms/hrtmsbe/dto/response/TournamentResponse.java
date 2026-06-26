package com.swp.hrtms.hrtmsbe.dto.response;

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
public class TournamentResponse {

    private Integer id;

    @JsonProperty("admin_id")
    private Integer adminId;

    private String name;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    //Khai
    @JsonProperty("announcement_date")
    private LocalDate announcementDate;

    @JsonProperty("registration_open_date")
    private LocalDate registrationOpenDate;

    @JsonProperty("registration_close_date")
    private LocalDate registrationCloseDate;

    @JsonProperty("allowed_breed")
    private String allowedBreed;

    @JsonProperty("allowed_horse_age")
    private Integer allowedHorseAge;

    @JsonProperty("tournament_description")
    private String description;

    private String status;

    @JsonProperty("cancel_reason")
    private String cancelReason;
}
