package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private com.swp.hrtms.hrtmsbe.enums.UserStatus status;
}
