package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.Data;

@Data
public class AdminRespondRequest {
    private String status; // "Accept" or "Reject"
    private String reason;
}
