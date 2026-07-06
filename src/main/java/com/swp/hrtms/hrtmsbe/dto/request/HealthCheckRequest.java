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
public class HealthCheckRequest {
    private Integer registrationFormId;
    private Integer doctorId;
    //khai
    private com.swp.hrtms.hrtmsbe.enums.HealthCheckStatus status; // "PENDING_DOCTOR", "ACCEPT", or "REJECT"
    private String medicalNotes;
    private java.time.LocalDateTime checkDate;
}

