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
public class RaceResultResponse {
    private Integer id;
    private Integer raceId;
    private Integer refereeId;
    private com.swp.hrtms.hrtmsbe.enums.RaceResultStatus status;
    private LocalDateTime createdAt;
    private String photoFinishImage;
}




