package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "horse_owners")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorseOwner {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "owner_name", nullable = true)
    private String ownerName;

    @jakarta.persistence.Lob
    @Column(columnDefinition = "TEXT", nullable = true)
    private String avatar;

    // private String phone;
}
