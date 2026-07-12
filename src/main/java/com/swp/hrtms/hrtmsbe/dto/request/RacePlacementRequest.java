package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RacePlacementRequest {
    private Integer raceResultId;
    private Integer registrationFormId;
    private Integer finishPosition;
    private LocalDateTime finishTime; // thời điểm ngựa về đích — FE truyền lên
    private Double weighInWeight; // cân nặng kiểm tra tại cổng đích
    // KHÔNG cần: id (server tự generate)
}

