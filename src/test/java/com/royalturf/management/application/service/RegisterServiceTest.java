package com.royalturf.management.application.service;

import com.royalturf.management.domain.model.Role;
import com.royalturf.management.domain.model.User;
import com.royalturf.management.domain.model.UserStatus;
import com.royalturf.management.domain.model.Wallet;
import com.royalturf.management.domain.port.inbound.RegisterUseCase;
import com.royalturf.management.domain.port.outbound.PasswordEncoderPort;
import com.royalturf.management.domain.port.outbound.UserRepositoryPort;
import com.royalturf.management.domain.port.outbound.WalletRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterServiceTest {

    private UserRepositoryPort userRepositoryPort;
    private WalletRepositoryPort walletRepositoryPort;
    private PasswordEncoderPort passwordEncoderPort;
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        userRepositoryPort = mock(UserRepositoryPort.class);
        walletRepositoryPort = mock(WalletRepositoryPort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        registerService = new RegisterService(userRepositoryPort, walletRepositoryPort, passwordEncoderPort);
    }

    @Test
    void register_SpectatorRole_ShouldCreateUserAndDefaultWallet() {
        // Arrange
        RegisterUseCase.Command command = new RegisterUseCase.Command(
                "john_doe", "john@example.com", "secure123", "SPECTATOR"
        );

        when(userRepositoryPort.existsByUsername(command.username())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoderPort.encode(command.password())).thenReturn("hashed_password");

        User savedUserMock = User.builder()
                .id(99L)
                .username(command.username())
                .email(command.email())
                .password("hashed_password")
                .role(Role.SPECTATOR)
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUserMock);

        // Act
        User result = registerService.register(command);

        // Assert
        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("john_doe", result.getUsername());
        assertEquals(Role.SPECTATOR, result.getRole());
        assertEquals(UserStatus.ACTIVE, result.getStatus());

        // Verify password was encoded
        verify(passwordEncoderPort).encode("secure123");

        // Verify User was saved with encoded password
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepositoryPort).save(userCaptor.capture());
        assertEquals("hashed_password", userCaptor.getValue().getPassword());

        // Verify Wallet was initialized and saved for Spectator (BR_14)
        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepositoryPort).save(walletCaptor.capture());
        Wallet savedWallet = walletCaptor.getValue();
        assertEquals(99L, savedWallet.getUserId());
        assertEquals(1000, savedWallet.getBalance());
        assertNotNull(savedWallet.getUpdatedAt());
    }

    @Test
    void register_HorseOwnerRole_ShouldCreateUserButNoWallet() {
        // Arrange
        RegisterUseCase.Command command = new RegisterUseCase.Command(
                "owner1", "owner1@example.com", "pass123", "HORSE_OWNER"
        );

        when(userRepositoryPort.existsByUsername(command.username())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(command.email())).thenReturn(false);
        when(passwordEncoderPort.encode(command.password())).thenReturn("hashed_password");

        User savedUserMock = User.builder()
                .id(100L)
                .username(command.username())
                .email(command.email())
                .password("hashed_password")
                .role(Role.HORSE_OWNER)
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepositoryPort.save(any(User.class))).thenReturn(savedUserMock);

        // Act
        User result = registerService.register(command);

        // Assert
        assertNotNull(result);
        assertEquals(Role.HORSE_OWNER, result.getRole());

        // Verify user saved, but NO wallet is created (Wallets are only initialized for SPECTATOR)
        verify(userRepositoryPort).save(any(User.class));
        verify(walletRepositoryPort, never()).save(any(Wallet.class));
    }

    @Test
    void register_UsernameExists_ShouldThrowException() {
        // Arrange
        RegisterUseCase.Command command = new RegisterUseCase.Command(
                "john_doe", "john@example.com", "secure123", "SPECTATOR"
        );
        when(userRepositoryPort.existsByUsername(command.username())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> registerService.register(command));
        assertEquals("Username is already taken", ex.getMessage());

        verify(userRepositoryPort, never()).save(any(User.class));
        verify(walletRepositoryPort, never()).save(any(Wallet.class));
    }

    @Test
    void register_EmailExists_ShouldThrowException() {
        // Arrange
        RegisterUseCase.Command command = new RegisterUseCase.Command(
                "john_doe", "john@example.com", "secure123", "SPECTATOR"
        );
        when(userRepositoryPort.existsByUsername(command.username())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(command.email())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> registerService.register(command));
        assertEquals("Email is already registered", ex.getMessage());

        verify(userRepositoryPort, never()).save(any(User.class));
        verify(walletRepositoryPort, never()).save(any(Wallet.class));
    }

    @Test
    void register_InvalidRole_ShouldThrowException() {
        // Arrange
        RegisterUseCase.Command command = new RegisterUseCase.Command(
                "john_doe", "john@example.com", "secure123", "SUPER_HERO"
        );
        when(userRepositoryPort.existsByUsername(command.username())).thenReturn(false);
        when(userRepositoryPort.existsByEmail(command.email())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> registerService.register(command));
        assertTrue(ex.getMessage().contains("Invalid role"));

        verify(userRepositoryPort, never()).save(any(User.class));
        verify(walletRepositoryPort, never()).save(any(Wallet.class));
    }
}
