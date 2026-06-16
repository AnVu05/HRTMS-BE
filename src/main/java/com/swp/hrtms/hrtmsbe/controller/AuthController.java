package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.LoginRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RegisterRequest;
import com.swp.hrtms.hrtmsbe.dto.request.VerifyOtpRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.LoginResponse;
import com.swp.hrtms.hrtmsbe.dto.response.UserResponse;
import com.swp.hrtms.hrtmsbe.dto.response.VerifyOtpResponse;
import com.swp.hrtms.hrtmsbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        UserResponse userResponse = userService.register(request);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .status(String.valueOf(HttpStatus.CREATED.value()))
                .message("User registered successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .message("Verification code sent to your email")
                .data(loginResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse verifyOtpResponse = userService.verifyOtp(request);
        ApiResponse<VerifyOtpResponse> apiResponse = ApiResponse.<VerifyOtpResponse>builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .message("Login successful")
                .data(verifyOtpResponse)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityContextHolder.clearContext();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(String.valueOf(HttpStatus.OK.value()))
                .message("Logout successful")
                .data(null)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
