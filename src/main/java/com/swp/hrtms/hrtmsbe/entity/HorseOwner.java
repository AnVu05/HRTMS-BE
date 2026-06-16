package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    // @Column(name = "owner_name", nullable = false)
    // private String ownerName;

    // cai nay hong can thiet - Thien xoa
    // private String phone;

}
