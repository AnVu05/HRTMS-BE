package com.swp.hrtms.hrtmsbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceRequest {
    @JsonProperty("tournament_id")
    private Integer tournamentId;

    private String name;

    private LocalDate date;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    @JsonProperty("distance_m")
    private Integer distanceM;

    @JsonProperty("horse_breed")
    private String horseBreed;

    @JsonProperty("weight_kg")
    private BigDecimal weightKg;

    @JsonProperty("horse_age")
    private Integer horseAge;

    @JsonProperty("betting_reward")
    private Long bettingReward;

    @JsonProperty("jockey_prizes")
    private java.util.List<RacePrizeRequest> jockeyPrizes;

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
