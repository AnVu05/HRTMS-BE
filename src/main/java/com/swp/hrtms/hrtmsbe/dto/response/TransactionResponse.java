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
public class TransactionResponse {
    private Integer id;
    private Integer walletId;
    private Integer tournamentId;
    private Integer raceId;
    private Integer horseId;
    private Integer amount;
    private String type;
    private LocalDateTime createdAt;
}

