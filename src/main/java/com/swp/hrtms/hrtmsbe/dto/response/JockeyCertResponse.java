package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
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

