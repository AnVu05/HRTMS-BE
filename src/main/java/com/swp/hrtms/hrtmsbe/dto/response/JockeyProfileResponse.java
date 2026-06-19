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
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String jockeyName;
    private Integer yearOfExperience;
    private Integer age;
    private String professionalBio;
    private Boolean status;
}
