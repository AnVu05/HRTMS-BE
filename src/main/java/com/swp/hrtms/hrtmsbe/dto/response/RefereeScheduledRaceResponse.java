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
public class RefereeScheduledRaceResponse {
    private Integer id;
    private Integer tournamentId;
    private String tournamentName;
    private String name;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer laps;
    private Integer numHorse;
    private com.swp.hrtms.hrtmsbe.enums.RaceStatus status;
    private String track;
}
