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

    //Khai: Required dates from the Create New Tournament form.
    @JsonProperty("announcement_date")
    private LocalDate announcementDate;

    @JsonProperty("registration_open_date")
    private LocalDate registrationOpenDate;

    @JsonProperty("registration_close_date")
    private LocalDate registrationCloseDate;

    @JsonProperty("tournament_description")
    private String description;
}
