package com.swp.hrtms.hrtmsbe.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "open_prediction_date")
    private LocalDate openPredictionDate;

    @Column(name = "close_prediction_date")
    private LocalDate closePredictionDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.TournamentStatus status = com.swp.hrtms.hrtmsbe.enums.TournamentStatus.DRAFT;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;
}
