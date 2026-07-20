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
public class AdminDashboardStatsResponse {
    private TournamentStats tournamentStats;
    private List<MonthlyRaceCount> racesThisMonth;
    private List<FillRate> raceFillRate;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TournamentStats {
        private long upcoming; // DRAFT
        private long ongoing; // PUBLISHED
        private long completed; // COMPLETE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRaceCount {
        private String date;
        private long count;
        private List<String> raceNames;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FillRate {
        private String name;
        private double value;
    }
}
