package com.swp.hrtms.hrtmsbe.entity;


// Copied by Kháº£i from HRTMS_BE_on_time-main
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
    @Column(name = "cert_image_base64", columnDefinition = "VARCHAR(MAX)")
    private String certImageBase64;

    @Column(name = "issued_at")
    private java.time.LocalDate issuedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    //khai
    private com.swp.hrtms.hrtmsbe.enums.CertificateStatus status = com.swp.hrtms.hrtmsbe.enums.CertificateStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jockey_id")
    private Jockey jockey;
}




