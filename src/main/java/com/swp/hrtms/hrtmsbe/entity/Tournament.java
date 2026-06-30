package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Khai: Dates displayed on the Create New Tournament form.
    @Column(name = "announcement_date")
    private LocalDate announcementDate;

    @Column(name = "registration_open_date")
    private LocalDate registrationOpenDate;

    @Column(name = "registration_close_date")
    private LocalDate registrationCloseDate;

    @Column(name = "allowed_breed")
    private String allowedBreed;

    @Column(name = "allowed_horse_age")
    private Integer allowedHorseAge;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String status; // "PUBLIC", "COMPLETED", "CANCELLED"

    @Column(name = "cancel_reason")
    private String cancelReason;
}
