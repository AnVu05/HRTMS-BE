package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationFormRequest {
    private Integer ownerId;
    private Integer horseId;
    private Integer jockeyId;
    private Integer tournamentId;
    private Integer raceId;
    //khai
    private Integer adminId;
    private com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus status;
    private LocalDateTime createdAt;
}

