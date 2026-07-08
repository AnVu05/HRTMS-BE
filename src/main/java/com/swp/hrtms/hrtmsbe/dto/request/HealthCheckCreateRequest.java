package com.swp.hrtms.hrtmsbe.dto.request;

import com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthCheckCreateRequest {
    private Integer registrationFormId;
    private Integer doctorId;
    private HealthCheckStatus status;
    private String medicalNotes;
    private LocalDateTime checkDate;
}
