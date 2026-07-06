package com.swp.hrtms.hrtmsbe.dto.response;


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
public class HealthCheckResponse {
    private Integer id;
    private Integer registrationFormId;
    private Integer doctorId;
    private com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus status;
    private String medicalNotes;
    private LocalDateTime checkDate;
}

