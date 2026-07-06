package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.Data;

@Data
public class AdminRequest {
    private String avatar;
    private String username;
    private String password;
    private String email;
    private String role;
    private java.time.LocalDateTime createdAt;
    private com.swp.hrtms.hrtmsbe.enums.UserStatus status;
}

