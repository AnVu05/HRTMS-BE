package com.swp.hrtms.hrtmsbe.dto.response;

import com.swp.hrtms.hrtmsbe.entity.HorseStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseResponse {

    private Integer id;
    private Integer ownerId;
    private String ownerName;
    private String name;
    private Integer age;
    private String breed;
    private String sex;
    private java.math.BigDecimal weightKg;
    private HorseStatus status;

}

