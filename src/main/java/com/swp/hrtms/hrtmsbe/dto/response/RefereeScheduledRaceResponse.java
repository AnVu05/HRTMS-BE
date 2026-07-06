package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Integer numHorse;
    private Integer distanceM;
    private Integer refereeId;
    private String refereeName;
    private com.swp.hrtms.hrtmsbe.enums.RaceStatus status;
    private String reason;
    private Integer raceRulesId;
    private String raceRulesName;
    private Integer expectedDurationMinutes;
    private Integer breakTimeMinutes;
    private LocalDateTime canceledAt;
}
