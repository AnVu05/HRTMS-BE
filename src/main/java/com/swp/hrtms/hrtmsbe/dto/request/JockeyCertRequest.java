package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.Data;

@Data
public class JockeyCertRequest {
    private String certName;
    private String certImageBase64;
    private java.time.LocalDate issuedAt;
    private Integer jockeyId;
}

