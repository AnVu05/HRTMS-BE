package com.swp.hrtms.hrtmsbe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//Rewrite for authentication & authorization: login returns JWT token and user info directly
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private UserResponse user;
    private boolean otpRequired;
    private String email;
}

