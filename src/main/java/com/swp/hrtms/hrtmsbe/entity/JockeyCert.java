package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "jockey_certs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JockeyCert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cert_name")
    private String certName;

    @Lob
    @Column(name = "cert_img", columnDefinition = "TEXT")
    private String certImg;

    @Builder.Default
    private String status = "PENDING";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jockey_id")
    private Jockey jockey;
}
