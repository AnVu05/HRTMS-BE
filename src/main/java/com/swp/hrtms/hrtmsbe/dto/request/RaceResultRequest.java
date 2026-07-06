package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceResultRequest {
    private Integer raceId;
    private Integer refereeId;
    //khai
    private com.swp.hrtms.hrtmsbe.enums.RaceResultStatus status; // "TEMPORARY" or "OFFICIAL"
    private java.time.LocalDateTime createdAt;
}


