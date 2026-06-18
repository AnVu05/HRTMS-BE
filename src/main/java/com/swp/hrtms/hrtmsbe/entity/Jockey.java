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
    @Column(name = "year_of_experience")
    private Integer yearOfExperience;

    @Min(value = 0, message = "Age must be greater than or equal to 0")
    private Integer age;

    @Column(name = "professional_bio", columnDefinition = "TEXT")
    private String professionalBio;

    @Column(columnDefinition = "bit")
    private Boolean status;

    @OneToMany(mappedBy = "jockey", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<JockeyCert> jockeyCerts;

    public void setStatus(Boolean status) {
        this.status = status;
        if (Boolean.FALSE.equals(status) && this.jockeyCerts != null) {
            for (JockeyCert cert : this.jockeyCerts) {
                cert.setStatus("REJECTED");
            }
        }
    }
}
