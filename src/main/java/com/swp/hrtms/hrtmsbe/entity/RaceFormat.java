package com.swp.hrtms.hrtmsbe.entity;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raceformats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RaceFormat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus status = com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus.ACTIVE;
}





