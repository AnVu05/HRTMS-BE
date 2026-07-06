package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpectatorProfileUpdateRequest {

    private String username;

    private String email;

    private String password;

    @com.fasterxml.jackson.annotation.JsonProperty("display_name")
    private String displayName;

    private String avatar;

    private String role;

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private java.time.LocalDateTime createdAt;
}

