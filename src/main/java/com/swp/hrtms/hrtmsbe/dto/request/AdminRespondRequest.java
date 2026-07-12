package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.Data;

@Data
public class AdminRespondRequest {
    private String status; // "Accept" or "Reject"
    private String reason;
}
