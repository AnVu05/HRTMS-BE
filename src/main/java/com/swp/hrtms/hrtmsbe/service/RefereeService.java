package com.swp.hrtms.hrtmsbe.service;

import com.swp.hrtms.hrtmsbe.dto.response.RefereeResponse;
import com.swp.hrtms.hrtmsbe.dto.response.RefereeScheduledRaceResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RefereeService {
    List<RefereeResponse> getReferees(LocalDate date, LocalTime startTime, LocalTime endTime, Integer excludeRaceId);
    List<RefereeScheduledRaceResponse> getScheduledRaces(Integer refereeId);
}
