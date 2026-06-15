package com.royalturf.management.infrastructure.adapter.outbound.jpa.mapper;

import com.royalturf.management.domain.model.User;
import com.royalturf.management.domain.model.Wallet;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.entity.UserJpaEntity;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.entity.WalletJpaEntity;

public final class JpaMapper {

    private JpaMapper() {
        // Prevent instantiation
    }

    public static User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }
        return UserJpaEntity.builder()
                .id(domain.getId())
                .username(domain.getUsername())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .role(domain.getRole())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public static Wallet toDomain(WalletJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Wallet.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .balance(entity.getBalance())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static WalletJpaEntity toEntity(Wallet domain) {
        if (domain == null) {
            return null;
        }
        return WalletJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .balance(domain.getBalance())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
