package com.swp.hrtms.hrtmsbe.entity;


import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(name = "prediction_time_before")
    private Integer predictionTimeBefore;
    @Column(name = "health_check_time_before")
    private Integer healthCheckTimeBefore;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus status = com.swp.hrtms.hrtmsbe.enums.RaceFormatStatus.ACTIVE;
}