package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RacePlacementResponse {
    private Integer id;
    private Integer raceResultId;
    private Integer registrationFormId;
    private Integer finishPosition;
    private LocalDateTime finishTime;
    private Double weighInWeight;
}

