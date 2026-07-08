package com.swp.hrtms.hrtmsbe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JockeyProfileResponse {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String jockeyName;
    private Integer experienceYears;
    private Integer age;
    private String professionalBio;
    // khai
    private com.swp.hrtms.hrtmsbe.enums.UserStatus status;
    private String avatar;
}
