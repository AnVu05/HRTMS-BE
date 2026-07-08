package com.swp.hrtms.hrtmsbe.service.impl;


// Copied by Kháº£i from HRTMS_BE_on_time-main
import com.swp.hrtms.hrtmsbe.dto.response.RefereeResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeScheduledRaceResponse;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.exception.ResourceNotFoundException;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
import com.swp.hrtms.hrtmsbe.repository.RaceRepository;
import com.swp.hrtms.hrtmsbe.service.RefereeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefereeServiceImpl implements RefereeService {

    private final RefereeRepository refereeRepository;
    private final RaceRepository raceRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RefereeResponse> getReferees(LocalDate date, LocalTime startTime, LocalTime endTime,
            Integer excludeRaceId) {
        List<Referee> referees;

        if (date != null && startTime != null && endTime != null) {
            if (excludeRaceId != null) {
                referees = refereeRepository.findAvailableRefereesExcludingRace(date, startTime, endTime,
                        excludeRaceId);
            } else {
                referees = refereeRepository.findAvailableReferees(date, startTime, endTime);
            }
        } else {
            referees = refereeRepository.findAll();
        }

        return referees.stream()
                .map(r -> RefereeResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách lịch thi đấu đã xác nhận (My Scheduled Races) của Trọng tài đó.
     * Chỉ lấy các cuộc đua có trạng thái "PUBLISHED" (tức là đã đồng ý tham gia
     * điều hành).
     *
     * @param refereeId ID của Trọng tài
     * @return Danh sách DTO chứa thông tin các cuộc đua đã xếp lịch
     */
    @Override
    @Transactional(readOnly = true)
    public List<RefereeScheduledRaceResponse> getScheduledRaces(Integer refereeId) {
        // Kiểm tra xem Trọng tài có tồn tại trong hệ thống hay không
        if (!refereeRepository.existsById(refereeId)) {
            throw new ResourceNotFoundException("Referee not found with id: " + refereeId);
        }

        // Lấy danh sách các cuộc đua đã xếp lịch của Trọng tài
        List<com.swp.hrtms.hrtmsbe.entity.Race> races = raceRepository.findScheduledRacesByRefereeId(refereeId);

        // Ánh xạ danh sách cuộc đua sang DTO trả về cho Client
        return races.stream().map(race -> {
            String tournamentName = (race.getTournament() != null) ? race.getTournament().getName() : null;
            Integer refId = (race.getReferee() != null) ? race.getReferee().getId() : null;
            String refName = (race.getReferee() != null) ? race.getReferee().getName() : null;
            Integer rulesId = (race.getRaceRules() != null) ? race.getRaceRules().getId() : null;
            String rulesName = (race.getRaceRules() != null) ? race.getRaceRules().getName() : null;

            return RefereeScheduledRaceResponse.builder()
                    .id(race.getId())
                    .tournamentId(race.getTournament() != null ? race.getTournament().getId() : null)
                    .tournamentName(tournamentName)
                    .name(race.getName())
                    .date(race.getDate())
                    .startTime(race.getStartTime())
                    .endTime(race.getEndTime())
                    .distanceM(race.getDistanceM())
                    .numHorse(race.getNumHorse())
                    .refereeId(refId)
                    .refereeName(refName)
                    .status(race.getStatus())
                    .reason(race.getReason())
                    .raceRulesId(rulesId)
                    .raceRulesName(rulesName)
                    .expectedDurationMinutes(race.getExpectedDurationMinutes())
                    .breakTimeMinutes(race.getBreakTimeMinutes())
                    .canceledAt(race.getCanceledAt())
                    .build();
        }).toList();
    }
}