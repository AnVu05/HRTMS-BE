package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Khai
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "races")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(nullable = false)
    private String name;

    // luu ngay tao
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "num_horse")
    private Integer numHorse;

    // Khai: Race requirements displayed on the Add New Race form.
    @Column(name = "distance_m")
    private Integer distanceM;

    // @Column(name = "horse_breed")
    // private String horseBreed;

    // @Column(name = "weight_kg", precision = 10, scale = 2)
    // private BigDecimal weightKg;

    // @Column(name = "horse_age")
    // private Integer horseAge;

    // @Column(name = "betting_reward")
    // private Long bettingReward;

    // Khai
    // @OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // @Builder.Default
    // private List<RacePrize> jockeyPrizes = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referee_id")
    private Referee referee;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private com.swp.hrtms.hrtmsbe.enums.RaceStatus status = com.swp.hrtms.hrtmsbe.enums.RaceStatus.PENDING_REFEREE;

    // private String track;

    @Column(name = "reason")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_rules")
    private RaceFormat raceRules;

    @Column(name = "expected_duration_minutes")
    private Integer expectedDurationMinutes;

    @Column(name = "break_time_minutes")
    private Integer breakTimeMinutes;

    @Column(name = "canceled_at")
    private java.time.LocalDateTime canceledAt;
}





