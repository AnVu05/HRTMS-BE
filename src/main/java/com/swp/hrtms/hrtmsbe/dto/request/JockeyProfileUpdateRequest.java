package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JockeyProfileUpdateRequest {
    private String jockeyName;
    private Integer yearOfExperience;
    private Integer age;
    private String professionalBio;
}
