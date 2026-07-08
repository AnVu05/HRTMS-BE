package com.swp.hrtms.hrtmsbe.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Integer id;
    private Integer userId;
    private Integer balance;
    private LocalDateTime updatedAt;
}

