package com.swp.hrtms.hrtmsbe.dto.request;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Khải: DTO nhận dữ liệu cập nhật profile của chủ ngựa.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorseOwnerProfileUpdateRequest {

    private String username;

    private String email;

    private String password;

    private String ownerName;

    private String avatar;

    private String role;

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private java.time.LocalDateTime createdAt;
}

