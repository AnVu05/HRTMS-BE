package com.swp.hrtms.hrtmsbe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationFormResponse {
    private Integer id;
    private Integer ownerId;
    private Integer horseId;
    private Integer jockeyId;
    private Integer tournamentId;
    private Integer raceId;
    private Integer adminId;
    
    @com.fasterxml.jackson.annotation.JsonProperty("owner_name")
    private String ownerName;

    @com.fasterxml.jackson.annotation.JsonProperty("horse_name")
    private String horseName;

    @com.fasterxml.jackson.annotation.JsonProperty("jockey_name")
    private String jockeyName;

    @com.fasterxml.jackson.annotation.JsonProperty("tournament_name")
    private String tournamentName;

    @com.fasterxml.jackson.annotation.JsonProperty("race_name")
    private String raceName;

    private com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status;
    private LocalDateTime createdAt;
}
