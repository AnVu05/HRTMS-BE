package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer balance;
    private java.time.LocalDateTime updatedAt;
}



