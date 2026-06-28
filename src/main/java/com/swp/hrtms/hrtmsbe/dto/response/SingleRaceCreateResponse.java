package com.swp.hrtms.hrtmsbe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Khai
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;

//Khai: Dedicated response for POST /api/v1/races.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleRaceCreateResponse {

    private Integer id;

    @JsonProperty("tournament_id")
    private Integer tournamentId;

    @JsonProperty("race_name")
    private String raceName;

    private LocalDate date;

    @JsonProperty("distance_m")
    private Integer distanceM;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    @JsonProperty("horse_breed")
    private String horseBreed;

    @JsonProperty("weight_kg")
    private BigDecimal weightKg;

    @JsonProperty("horse_age")
    private Integer horseAge;

    //Khai
    @JsonProperty("jockey_prizes")
    private List<RacePrizeResponse> jockeyPrizes;

    @JsonProperty("betting_reward")
    private Long bettingReward;

    @JsonProperty("referee_id")
    private Integer refereeId;

    private String status;
}
