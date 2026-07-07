package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "horses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Horse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private HorseOwner owner;

    @Column(nullable = false)
    private String name;

    private Integer age;

    private String breed;

    @Column(length = 20)
    private String sex;

    @Column(nullable = false, columnDefinition = "DECIMAL(5,2)")
    private java.math.BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    private HorseStatus status; //thêm class HorseStatus để ko bao giờ gõ sai status của ngựa
}




