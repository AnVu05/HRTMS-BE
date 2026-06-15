package com.royalturf.management.infrastructure.config;

import com.royalturf.management.application.service.RegisterService;
import com.royalturf.management.domain.port.inbound.RegisterUseCase;
import com.royalturf.management.domain.port.outbound.PasswordEncoderPort;
import com.royalturf.management.domain.port.outbound.UserRepositoryPort;
import com.royalturf.management.domain.port.outbound.WalletRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public RegisterUseCase registerUseCase(
            UserRepositoryPort userRepositoryPort,
            WalletRepositoryPort walletRepositoryPort,
            PasswordEncoderPort passwordEncoderPort
    ) {
        return new RegisterService(userRepositoryPort, walletRepositoryPort, passwordEncoderPort);
    }
}
