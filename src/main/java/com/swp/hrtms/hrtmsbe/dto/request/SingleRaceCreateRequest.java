package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Khai
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

    //Khai: Fields required by the Add New Race form.
    @JsonProperty("distance_m")
    private Integer distanceM;

    @JsonProperty("num_horse")
    private Integer numHorse;

    @JsonProperty("referee_id")
    private Integer refereeId;

    @JsonProperty("race_rules_id")
    private Integer raceRulesId;

    @JsonProperty("expected_duration_minutes")
    private Integer expectedDurationMinutes;

    @JsonProperty("break_time_minutes")
    private Integer breakTimeMinutes;
}

