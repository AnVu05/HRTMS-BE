package com.swp.hrtms.hrtmsbe.dto.response;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpectatorProfileResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Integer userId;

    private String displayName;

    private String username;

    private String password;

    private String email;

    private String role;

    private LocalDateTime createdAt;

    private String avatar;
}

