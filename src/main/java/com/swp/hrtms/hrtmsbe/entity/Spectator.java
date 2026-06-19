package com.swp.hrtms.hrtmsbe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "spectators")
@PrimaryKeyJoinColumn(name = "user_id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Spectator extends User {

    @Column(name = "display_name")
    private String displayName;

    @Lob
    @Column(name = "avatar_image", columnDefinition = "TEXT")
    private String avatarImage;

    @Column(name = "avatar_content_type")
    private String avatarContentType;
}
