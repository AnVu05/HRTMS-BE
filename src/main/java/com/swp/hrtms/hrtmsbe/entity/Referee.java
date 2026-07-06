package com.swp.hrtms.hrtmsbe.entity;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "referees")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Referee extends User {
    private String name;
}



