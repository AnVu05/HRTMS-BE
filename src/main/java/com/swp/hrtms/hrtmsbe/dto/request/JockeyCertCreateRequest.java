package com.swp.hrtms.hrtmsbe.dto.request;

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
}
