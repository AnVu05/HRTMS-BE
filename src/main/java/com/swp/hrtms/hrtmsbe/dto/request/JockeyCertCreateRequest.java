package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Khai: Request body for creating a jockey certificate from a Base64 image.
public class JockeyCertCreateRequest {

    private String certName;

    private String certImageBase64;

    private java.time.LocalDate issuedAt;
    private com.swp.hrtms.hrtmsbe.enums.CertificateStatus status;
    private Integer jockeyId;
}

