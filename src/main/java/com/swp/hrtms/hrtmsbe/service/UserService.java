package com.swp.hrtms.hrtmsbe.service;


// Copied by Kháº£i from HRTMS_BE_on_time-main
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
    java.util.List<UserResponse> getAllUsers();
    java.util.List<UserResponse> getAllUsersExcludeCurrent(Integer currentUserId);
    UserResponse getUserById(Integer id);
    UserResponse updateUser(Integer id, com.swp.hrtms.hrtmsbe.dto.request.UserUpdateRequest request);
}


