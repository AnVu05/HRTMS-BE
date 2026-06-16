package com.swp.hrtms.hrtmsbe.controller;

import com.swp.hrtms.hrtmsbe.dto.request.RegisterRequest;
import com.swp.hrtms.hrtmsbe.dto.response.ApiResponse;
import com.swp.hrtms.hrtmsbe.dto.response.UserResponse;
import com.swp.hrtms.hrtmsbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                .status(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(userResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
