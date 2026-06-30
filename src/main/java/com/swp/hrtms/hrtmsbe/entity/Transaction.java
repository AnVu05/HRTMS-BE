package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer walletId;
    private Integer tournamentId;
    private Integer raceId;
    private Integer horseId;
    private Integer amount;
    private String type;
    private java.time.LocalDateTime createdAt;
}
