package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefereeInvitationResponse {
    private Integer notificationId;
    private Integer raceId;
    private String raceName;
    private String tournamentName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String track;
}
