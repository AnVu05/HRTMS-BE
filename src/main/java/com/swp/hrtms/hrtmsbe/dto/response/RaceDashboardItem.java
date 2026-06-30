package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceDashboardItem {
    private Integer id;
    private String name;
    private java.time.LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer laps;
    private com.swp.hrtms.hrtmsbe.enums.RaceStatus status;
    private Integer refereeId;
    private String refereeName;
    private String track;
}
