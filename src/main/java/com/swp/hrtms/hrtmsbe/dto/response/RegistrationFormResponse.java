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
    private com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status;
    private LocalDateTime createdAt;
}
