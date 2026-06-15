package com.royalturf.management.infrastructure.adapter.outbound.jpa;

import com.royalturf.management.domain.model.Wallet;
import com.royalturf.management.domain.port.outbound.WalletRepositoryPort;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.entity.WalletJpaEntity;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.mapper.JpaMapper;
import com.royalturf.management.infrastructure.adapter.outbound.jpa.repository.SpringDataWalletRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WalletJpaAdapter implements WalletRepositoryPort {

    private final SpringDataWalletRepository springDataWalletRepository;

    public WalletJpaAdapter(SpringDataWalletRepository springDataWalletRepository) {
        this.springDataWalletRepository = springDataWalletRepository;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletJpaEntity entity = JpaMapper.toEntity(wallet);
        WalletJpaEntity savedEntity = springDataWalletRepository.save(entity);
        return JpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Wallet> findByUserId(Long userId) {
        return springDataWalletRepository.findByUserId(userId)
                .map(JpaMapper::toDomain);
    }
}
