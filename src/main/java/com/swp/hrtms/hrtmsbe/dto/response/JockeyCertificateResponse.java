package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Khai: Response DTO for a jockey to view a certificate and its current verification status.
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JockeyCertificateResponse {

    @JsonProperty("cert_id")
    private Integer certId;

    @JsonProperty("cert_name")
    private String certName;

    @JsonProperty("cert_image_base64")
    private String certImageBase64;

    @JsonProperty("issued_at")
    private java.time.LocalDate issuedAt;

    private com.swp.hrtms.hrtmsbe.enums.CertificateStatus status;
}

