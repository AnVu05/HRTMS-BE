package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.*;
import com.swp.hrtms.hrtmsbe.dto.response.LoginResponse;
import com.swp.hrtms.hrtmsbe.dto.response.UserResponse;
import com.swp.hrtms.hrtmsbe.dto.response.VerifyOtpResponse;
import com.swp.hrtms.hrtmsbe.entity.*;
import com.swp.hrtms.hrtmsbe.repository.*;
import com.swp.hrtms.hrtmsbe.security.JwtUtil;
import com.swp.hrtms.hrtmsbe.service.EmailService;
import com.swp.hrtms.hrtmsbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HorseOwnerRepository horseOwnerRepository;
    private final DoctorRepository doctorRepository;
    private final WalletRepository walletRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final OtpCodeRepository otpCodeRepository;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // 1. Kiểm tra tính hợp lệ của tham số đầu vào
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (request.getRole() == null || request.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty");
        }

        String role = request.getRole().trim().toUpperCase();
        if (!role.equals("SPECTATOR") && !role.equals("HORSE_OWNER") && !role.equals("JOCKEY")
                && !role.equals("DOCTOR") && !role.equals("REFEREE")) {
            throw new IllegalArgumentException("Registration is not allowed for role: " + role);
        }

        // 2. Kiểm tra trùng lặp tài khoản
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        // 3. Tiến hành lưu thực thể tương ứng với từng vai trò
        User savedUser;
        if (role.equals("HORSE_OWNER")) {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword()); // plaintext theo yêu cầu
            user.setRole("HORSE_OWNER");
            savedUser = userRepository.save(user);

            HorseOwner horseOwner = HorseOwner.builder()
                    .user(savedUser)
                    .build();
            horseOwnerRepository.save(horseOwner);
        } else if (role.equals("SPECTATOR")) {
            Spectator spectator = new Spectator();
            spectator.setUsername(request.getUsername());
            spectator.setEmail(request.getEmail());
            spectator.setPassword(request.getPassword());
            spectator.setRole("SPECTATOR");
            spectator.setDisplayName(request.getUsername());
            savedUser = userRepository.save(spectator);

            // BR_14: Initial 1000 points to Spectator Wallet
            Wallet wallet = Wallet.builder()
                    .user(savedUser)
                    .balance(1000)
                    .updatedAt(LocalDateTime.now())
                    .build();
            walletRepository.save(wallet);
        } else if (role.equals("JOCKEY")) {
            Jockey jockey = new Jockey();
            jockey.setUsername(request.getUsername());
            jockey.setEmail(request.getEmail());
            jockey.setPassword(request.getPassword());
            jockey.setRole("JOCKEY");
            savedUser = userRepository.save(jockey);
        } else if (role.equals("REFEREE")) {
            com.swp.hrtms.hrtmsbe.entity.Referee referee = new com.swp.hrtms.hrtmsbe.entity.Referee();
            referee.setUsername(request.getUsername());
            referee.setEmail(request.getEmail());
            referee.setPassword(request.getPassword());
            referee.setRole("REFEREE");
            // Referee có trường name, nhưng RegisterRequest không có, ta tạm để null hoặc
            // username
            savedUser = userRepository.save(referee);
        } else if (role.equals("DOCTOR")) { // DOCTOR
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            user.setRole("DOCTOR");
            savedUser = userRepository.save(user);

            com.swp.hrtms.hrtmsbe.entity.Doctor doctor = com.swp.hrtms.hrtmsbe.entity.Doctor.builder()
                    .user(savedUser)
                    .build();
            doctorRepository.save(doctor);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        // Rewrite for authentication & authorization: Generate and send OTP immediately
        // upon registration
        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpCodeRepository.deleteByEmail(savedUser.getEmail());
        OtpCode otpCode = OtpCode.builder()
                .email(savedUser.getEmail())
                .code(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();
        otpCodeRepository.save(otpCode);
        emailService.sendOtp(savedUser.getEmail(), otp);

        // 4. Trả về kết quả sau khi đăng ký thành công
        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // Rewrite for authentication & authorization: Login directly issues JWT token
        // for ACTIVE users
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (user.getStatus() != com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not activated. Please verify your email first.");
        }

        // Generate JWT Token immediately on login
        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), user.getRole(), user.getId());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        return LoginResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    @Override
    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        // Rewrite for authentication & authorization: verifyOtp activates account, does
        // not issue token
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
            throw new IllegalArgumentException("OTP code cannot be empty");
        }

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OtpCode otpCode = otpCodeRepository.findTopByEmailAndCodeOrderByExpiryTimeDesc(
                request.getEmail().trim(), request.getOtpCode().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP code"));

        if (otpCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP code has expired");
        }

        // Xóa mã OTP sau khi xác thực thành công để không tái sử dụng
        otpCodeRepository.delete(otpCode);

        // Kích hoạt tài khoản
        user.setStatus(com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE);
        userRepository.save(user);

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        return VerifyOtpResponse.builder()
                .message("Account verified and activated successfully.")
                .user(userResponse)
                .build();
    }

    // Bo sung them Tinh Nang:
    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found with this email"));

        if (user.getStatus() != com.swp.hrtms.hrtmsbe.enums.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is locked or inactive");
        }

        // Tạo mã OTP ngẫu nhiên gồm 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Xóa các OTP cũ của user này trước khi lưu mới
        otpCodeRepository.deleteByEmail(user.getEmail());

        OtpCode otpCode = OtpCode.builder()
                .email(user.getEmail())
                .code(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();
        otpCodeRepository.save(otpCode);

        // Gửi OTP qua mail
        emailService.sendOtp(user.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
            throw new IllegalArgumentException("OTP code cannot be empty");
        }
        if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        User user = userRepository.findByEmail(request.getEmail().trim())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OtpCode otpCode = otpCodeRepository.findTopByEmailAndCodeOrderByExpiryTimeDesc(
                request.getEmail().trim(), request.getOtpCode().trim())
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP code"));

        if (otpCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP code has expired");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);

        // Xóa mã OTP sau khi đổi mật khẩu thành công để không tái sử dụng
        otpCodeRepository.delete(otpCode);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .status(user.getStatus().name())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<UserResponse> getAllUsersExcludeCurrent(Integer currentUserId) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .password(user.getPassword())
                        .role(user.getRole())
                        .status(user.getStatus().name())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Integer id, com.swp.hrtms.hrtmsbe.dto.request.UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (!user.getUsername().equals(request.getUsername())
                    && userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Need a method existsByEmailAndIdNot in userRepository if checking other
            // users' emails,
            // but for simplicity, we check existsByEmail if email changes.
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(request.getRole());
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .password(updatedUser.getPassword())
                .role(updatedUser.getRole())
                .status(updatedUser.getStatus().name())
                .createdAt(updatedUser.getCreatedAt())
                .build();
    }

}
