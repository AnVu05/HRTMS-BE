package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JockeyCertImageResponse {

    @JsonProperty("cert_image_base64")
    private String certImageBase64;
}

