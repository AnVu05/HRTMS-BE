package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.Data;

@Data
public class RaceFormatRequest {
    private String name;
    private String description;
    private Double entryFee;
    private Double firstPrizePercent;
    private Double secondPrizePercent;
    private Double thirdPrizePercent;
    private String allowedBreed;
    private Integer allowedHorseAge;
    private Integer minJockeyExperience;
    private Integer minWeight;
    private Integer maxWeight;
    private Integer baseWeight;
    private Integer applyFemaleAllowance;
    private com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus status;
}

