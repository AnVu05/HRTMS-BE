package com.swp.hrtms.hrtmsbe.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Column;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
// @NoArgsConstructor
// @AllArgsConstructor
public class Admin extends User {

    // @Column(name = "employee_code", nullable = false, unique = true)
    // private String employeeCode;

    @Lob
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String avatar;
}



