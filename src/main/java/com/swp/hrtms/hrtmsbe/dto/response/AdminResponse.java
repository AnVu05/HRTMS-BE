package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.Data;

@Data
public class AdminResponse {
    private Integer id;
    private String avatar;
    private String username;
    private String password;
    private String email;
    private String role;
    private java.time.LocalDateTime createdAt;
    private com.swp.hrtms.hrtmsbe.enums.UserStatus status;
}

