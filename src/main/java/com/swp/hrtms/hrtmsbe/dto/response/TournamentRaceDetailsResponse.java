package com.swp.hrtms.hrtmsbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentRaceDetailsResponse {
    private com.swp.hrtms.hrtmsbe.enums.TournamentStatus tournamentStatus;
    private Long totalEntries;
    private List<RaceDashboardItem> races;
}

