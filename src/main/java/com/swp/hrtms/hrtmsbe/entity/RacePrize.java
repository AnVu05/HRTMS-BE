package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "race_prizes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_race_prize_race_rank",
                columnNames = {"race_id", "rank_position"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RacePrize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @Column(name = "rank_position", nullable = false)
    private Integer rank;

    @Column(nullable = false)
    private Long amount;
}
