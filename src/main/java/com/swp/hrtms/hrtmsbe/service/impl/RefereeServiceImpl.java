package com.swp.hrtms.hrtmsbe.service.impl;

import com.swp.hrtms.hrtmsbe.dto.response.RefereeResponse;
import com.swp.hrtms.hrtmsbe.entity.Referee;
import com.swp.hrtms.hrtmsbe.repository.RefereeRepository;
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

    @Override
    @Transactional(readOnly = true)
    public List<RefereeResponse> getReferees(LocalDate date, LocalTime startTime, LocalTime endTime, Integer excludeRaceId) {
        List<Referee> referees;

        if (date != null && startTime != null && endTime != null) {
            if (excludeRaceId != null) {
                referees = refereeRepository.findAvailableRefereesExcludingRace(date, startTime, endTime, excludeRaceId);
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
}
