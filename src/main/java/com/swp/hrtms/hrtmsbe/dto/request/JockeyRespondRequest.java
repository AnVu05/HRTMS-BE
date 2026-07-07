package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.Data;

@Data
public class JockeyRespondRequest {
    private String status; // "Accept" or "Reject"
}
