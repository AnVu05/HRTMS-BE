package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Khai: Request body for updating a jockey certificate name and Base64 image.
public class JockeyCertUpdateRequest {

    private String certName;

    private String certImageBase64;
}
