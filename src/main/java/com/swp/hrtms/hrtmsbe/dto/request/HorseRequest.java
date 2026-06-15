package com.swp.hrtms.hrtmsbe.dto.request;

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
    private HorseStatus status;

    
}
