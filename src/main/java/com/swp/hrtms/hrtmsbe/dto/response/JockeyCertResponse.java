package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.Data;

@Data
public class JockeyCertResponse {
    private Integer id;
    private String certName;
    private String certImageBase64;
    private java.time.LocalDate issuedAt;
    private com.swp.hrtms.hrtmsbe.enums.CertificateStatus status;
    private Integer jockeyId;
}

