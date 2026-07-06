package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swp.hrtms.hrtmsbe.entity.HorseStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseRequest {
    @JsonProperty("owner_id")
    private Integer ownerId;
    
    private String name;
    private Integer age;
    private String breed;
    private String sex;
    private java.math.BigDecimal weightKg;
    private HorseStatus status;

    
}

