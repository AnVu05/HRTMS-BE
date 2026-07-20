package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.response.AdminDashboardStatsResponse;
import com.swp.hrtms.hrtmsbe.entity.Race;
import com.swp.hrtms.hrtmsbe.entity.RegistrationForm;
import com.swp.hrtms.hrtmsbe.enums.RegistrationFormStatus;
import com.swp.hrtms.hrtmsbe.enums.TournamentStatus;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.repository.RegistrationFormRepository;
import com.swp.hrtms.hrtmsbe.repository.TournamentRepository;
import com.swp.hrtms.hrtmsbe.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

        private final TournamentRepository tournamentRepository;
        private final RaceRepository raceRepository;
        private final RegistrationFormRepository registrationFormRepository;

        @Override
        public AdminDashboardStatsResponse getAdminDashboardStats() {
                AdminDashboardStatsResponse response = new AdminDashboardStatsResponse();

                // 1. Tournament Stats
                long draft = tournamentRepository.findByStatus(TournamentStatus.DRAFT).size();
                long published = tournamentRepository.findByStatus(TournamentStatus.PUBLISHED).size();
                long complete = tournamentRepository.findByStatus(TournamentStatus.COMPLETE).size();

                response.setTournamentStats(AdminDashboardStatsResponse.TournamentStats.builder()
                                .upcoming(draft)
                                .ongoing(published)
                                .completed(complete)
                                .build());

                // 2. Races This Month
                YearMonth currentMonth = YearMonth.now();
                LocalDate startDate = currentMonth.atDay(1);
                LocalDate endDate = currentMonth.atEndOfMonth();
                List<Race> racesThisMonth = raceRepository.findByDateBetween(startDate, endDate);

                // Group by date and collect race names
                Map<String, List<Race>> racesByDate = racesThisMonth.stream()
                        .filter(r -> r.getDate() != null)
                        .collect(Collectors.groupingBy(r -> r.getDate().toString()));

                List<AdminDashboardStatsResponse.MonthlyRaceCount> monthlyRaceCounts = new ArrayList<>();
                // Ensure all days of the month have a count
                for (int i = 1; i <= endDate.getDayOfMonth(); i++) {
                        LocalDate d = currentMonth.atDay(i);
                        String dStr = d.toString();
                        List<Race> dailyRaces = racesByDate.getOrDefault(dStr, new ArrayList<>());
                        long count = dailyRaces.size();
                        List<String> raceNames = dailyRaces.stream()
                                .map(Race::getName)
                                .collect(Collectors.toList());

                        monthlyRaceCounts.add(AdminDashboardStatsResponse.MonthlyRaceCount.builder()
                                .date(dStr)
                                .count(count)
                                .raceNames(raceNames)
                                .build());
                }
                response.setRacesThisMonth(monthlyRaceCounts);

                // 3. Race Fill Rate
                // Fetch all active/completed races
                List<Race> allRaces = raceRepository.findAll().stream()
                                .filter(r -> r.getStatus() != com.swp.hrtms.hrtmsbe.enums.RaceStatus.CANCELLED)
                                .collect(Collectors.toList());

                long totalSlots = 0;
                long totalRegistered = 0;
                int validRacesCount = 0;

                for (Race race : allRaces) {
                        if (race.getNumHorse() != null && race.getNumHorse() > 0) {
                                List<RegistrationForm> forms = registrationFormRepository
                                                .findByRace_IdAndStatus(race.getId(), RegistrationFormStatus.RACING);
                                totalSlots += race.getNumHorse();
                                totalRegistered += forms.size();
                                validRacesCount++;
                        }
                }

                double avgRegistered = validRacesCount > 0 ? (double) totalRegistered / validRacesCount : 0;
                double avgMaxSlots = validRacesCount > 0 ? (double) totalSlots / validRacesCount : 0;
                double avgEmpty = Math.max(0, avgMaxSlots - avgRegistered);

                List<AdminDashboardStatsResponse.FillRate> fillRates = new ArrayList<>();
                fillRates.add(AdminDashboardStatsResponse.FillRate.builder()
                                .name("Registered (Avg)")
                                .value(Math.round(avgRegistered * 100.0) / 100.0)
                                .build());
                fillRates.add(AdminDashboardStatsResponse.FillRate.builder()
                                .name("Empty Slots (Avg)")
                                .value(Math.round(avgEmpty * 100.0) / 100.0)
                                .build());

                response.setRaceFillRate(fillRates);

                return response;
        }
}
