package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Khải: DTO trả dữ liệu profile của chủ ngựa cho frontend, không trả password.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorseOwnerProfileResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Integer userId;

    private String username;

    private String password;

    private String email;

    private String role;

    private LocalDateTime createdAt;

    private String ownerName;

    private String avatar;
}

