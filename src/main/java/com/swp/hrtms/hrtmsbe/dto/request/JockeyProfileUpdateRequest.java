package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JockeyProfileUpdateRequest {
    private String jockeyName;
    private Integer experienceYears;
    private Integer age;
    private String professionalBio;
    private String avatar;
    private String username;
    private String password;
    private String email;
    private String role;
    private java.time.LocalDateTime createdAt;
    // khai
    private com.swp.hrtms.hrtmsbe.enums.UserStatus status;
}


