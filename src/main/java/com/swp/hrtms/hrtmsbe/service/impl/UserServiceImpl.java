package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.request.LoginRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RegisterRequest;
import com.swp.hrtms.hrtmsbe.dto.request.VerifyOtpRequest;
import com.swp.hrtms.hrtmsbe.dto.response.LoginResponse;
import com.swp.hrtms.hrtmsbe.dto.response.UserResponse;
import com.swp.hrtms.hrtmsbe.dto.response.VerifyOtpResponse;
import com.swp.hrtms.hrtmsbe.entity.HorseOwner;
import com.swp.hrtms.hrtmsbe.entity.Jockey;
import com.swp.hrtms.hrtmsbe.entity.OtpCode;
import com.swp.hrtms.hrtmsbe.entity.Spectator;
import com.swp.hrtms.hrtmsbe.entity.User;
import com.swp.hrtms.hrtmsbe.repository.HorseOwnerRepository;
import com.swp.hrtms.hrtmsbe.repository.OtpCodeRepository;
import com.swp.hrtms.hrtmsbe.repository.UserRepository;
import com.swp.hrtms.hrtmsbe.security.JwtUtil;
import com.swp.hrtms.hrtmsbe.service.EmailService;
import com.swp.hrtms.hrtmsbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HorseOwnerRepository horseOwnerRepository;
    private final OtpCodeRepository otpCodeRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

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
        if (!role.equals("SPECTATOR") && !role.equals("HORSE_OWNER") && !role.equals("JOCKEY")) {
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
        } else { // JOCKEY
            Jockey jockey = new Jockey();
            jockey.setUsername(request.getUsername());
            jockey.setEmail(request.getEmail());
            jockey.setPassword(request.getPassword());
            jockey.setRole("JOCKEY");
            savedUser = userRepository.save(jockey);
        }

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

        // Tạo mã OTP ngẫu nhiên gồm 6 chữ số
        String otp = String.format("%06d", new Random().nextInt(1000000));

        // Xóa các OTP cũ của email này trước khi lưu mới
        otpCodeRepository.deleteByEmail(user.getEmail());

        OtpCode otpCode = OtpCode.builder()
                .email(user.getEmail())
                .code(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();
        otpCodeRepository.save(otpCode);

        // Gửi OTP qua mail
        emailService.sendOtp(user.getEmail(), otp);

        return LoginResponse.builder()
                .otpRequired(true)
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
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

        // Tạo JWT Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getEmail(), user.getRole(), user.getId());

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        return VerifyOtpResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }
}
