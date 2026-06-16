package com.swp.hrtms.hrtmsbe.dto.request;

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
}
