package com.royalturf.management.infrastructure.adapter.outbound.jpa.repository;

import com.royalturf.management.infrastructure.adapter.outbound.jpa.entity.WalletJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataWalletRepository extends JpaRepository<WalletJpaEntity, Long> {
    Optional<WalletJpaEntity> findByUserId(Long userId);
}
