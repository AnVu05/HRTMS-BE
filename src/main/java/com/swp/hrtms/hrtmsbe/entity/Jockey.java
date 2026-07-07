package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "jockeys")
@PrimaryKeyJoinColumn(name = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Jockey extends User {

    @Column(name = "jockey_name")
    private String jockeyName;

    @Min(value = 0, message = "Years of experience must be greater than or equal to 0")
    @Column(name = "experience_years")
    private Integer experienceYears;

    @Min(value = 0, message = "Age must be greater than or equal to 0")
    private Integer age;

    @Column(name = "professional_bio", columnDefinition = "TEXT")
    private String professionalBio;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String avatar;

    @OneToMany(mappedBy = "jockey", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<JockeyCert> jockeyCerts;
}
