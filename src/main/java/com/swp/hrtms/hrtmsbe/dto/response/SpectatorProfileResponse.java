package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpectatorProfileResponse {

    private Integer userId;

    private String displayName;

    private String username;

    private String email;

    private String role;

    private LocalDateTime createdAt;

    private String avatarBase64;

    private String avatarContentType;
}
