package com.swp.hrtms.hrtmsbe.dto.request;

import lombok.Data;

@Data
public class TransactionRequest {
    private Integer walletId;
    private Integer tournamentId;
    private Integer raceId;
    private Integer horseId;
    private Integer amount;
    private String type;
    // KHÔNG cần: id, createdAt (server tự set)
}

