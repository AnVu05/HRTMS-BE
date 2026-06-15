package com.royalturf.management.application.service;

import com.royalturf.management.domain.model.Role;
import com.royalturf.management.domain.model.User;
import com.royalturf.management.domain.model.UserStatus;
import com.royalturf.management.domain.model.Wallet;
import com.royalturf.management.domain.port.inbound.RegisterUseCase;
import com.royalturf.management.domain.port.outbound.PasswordEncoderPort;
import com.royalturf.management.domain.port.outbound.UserRepositoryPort;
import com.royalturf.management.domain.port.outbound.WalletRepositoryPort;

import java.time.LocalDateTime;

public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final WalletRepositoryPort walletRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public RegisterService(
            UserRepositoryPort userRepositoryPort,
            WalletRepositoryPort walletRepositoryPort,
            PasswordEncoderPort passwordEncoderPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.walletRepositoryPort = walletRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User register(Command command) {
        // 1. Validation for uniqueness
        if (userRepositoryPort.existsByUsername(command.username())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // 2. Parse and validate role
        Role role;
        try {
            role = Role.valueOf(command.role().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid role: " + command.role());
        }

        // 3. Encrypt password and construct User
        String encodedPassword = passwordEncoderPort.encode(command.password());
        User user = User.builder()
                .username(command.username())
                .email(command.email())
                .password(encodedPassword)
                .role(role)
                .status(UserStatus.ACTIVE) // Default active
                .createdAt(LocalDateTime.now())
                .build();

        // 4. Save User
        User savedUser = userRepositoryPort.save(user);

        // 5. Create default wallet for SPECTATOR role (BR_14: Khởi tạo vốn điểm)
        if (role == Role.SPECTATOR) {
            Wallet wallet = Wallet.builder()
                    .userId(savedUser.getId())
                    .balance(1000) // Default starting points
                    .updatedAt(LocalDateTime.now())
                    .build();
            walletRepositoryPort.save(wallet);
        }

        return savedUser;
    }
}
