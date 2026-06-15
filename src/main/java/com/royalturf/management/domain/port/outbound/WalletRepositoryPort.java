package com.royalturf.management.domain.port.outbound;

import com.royalturf.management.domain.model.Wallet;
import java.util.Optional;

public interface WalletRepositoryPort {
    Wallet save(Wallet wallet);
    Optional<Wallet> findByUserId(Long userId);
}
