package com.swp.hrtms.hrtmsbe.controller;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.request.LoginRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RegisterRequest;
import com.swp.hrtms.hrtmsbe.dto.request.VerifyOtpRequest;
import com.swp.hrtms.hrtmsbe.dto.request.ForgotPasswordRequest;
import com.swp.hrtms.hrtmsbe.dto.request.ResetPasswordRequest;
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

    //Rewrite for authentication & authorization: Update success messages for the corrected flow
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody RegisterRequest request) {
        UserResponse userResponse = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "User registered successfully. Please check your email for the verification code."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login successful."));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<VerifyOtpResponse>> verifyOtp(@RequestBody VerifyOtpRequest request) {
        VerifyOtpResponse verifyOtpResponse = userService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(verifyOtpResponse, "Account verified and activated successfully."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Verification code sent to your email"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful"));
    }
}


