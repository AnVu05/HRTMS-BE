package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Khai
import java.time.LocalDate;
import java.time.LocalTime;

//Khai: Dedicated response for POST /api/v1/races.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SingleRaceCreateResponse {

    private Integer id;

    @JsonProperty("tournament_id")
    private Integer tournamentId;

    @JsonProperty("race_name")
    private String raceName;

    private LocalDate date;

    @JsonProperty("distance_m")
    private Integer distanceM;

    @JsonProperty("num_horse")
    private Integer numHorse;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    @JsonProperty("referee_id")
    private Integer refereeId;

    private com.swp.hrtms.hrtmsbe.enums.RaceStatus status;

    private String reason;

    @JsonProperty("race_rules_id")
    private Integer raceRulesId;

    @JsonProperty("expected_duration_minutes")
    private Integer expectedDurationMinutes;

    @JsonProperty("break_time_minutes")
    private Integer breakTimeMinutes;

    @JsonProperty("canceled_at")
    private java.time.LocalDateTime canceledAt;
}

