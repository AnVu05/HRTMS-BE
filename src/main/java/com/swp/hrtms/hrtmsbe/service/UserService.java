package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.request.LoginRequest;
import com.swp.hrtms.hrtmsbe.dto.request.RegisterRequest;
import com.swp.hrtms.hrtmsbe.dto.request.VerifyOtpRequest;
import com.swp.hrtms.hrtmsbe.dto.response.LoginResponse;
import com.swp.hrtms.hrtmsbe.dto.response.UserResponse;
import com.swp.hrtms.hrtmsbe.dto.response.VerifyOtpResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    VerifyOtpResponse verifyOtp(VerifyOtpRequest request);
}


